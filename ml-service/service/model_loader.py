import os
import pickle
from dataclasses import dataclass

import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.ensemble import RandomForestRegressor
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder


def _safe_float(v, default=0.0):
  try:
    if v is None:
      return default
    return float(v)
  except Exception:
    return default


def _dispatch_hour(dispatch_time):
  if not dispatch_time:
    return 12
  try:
    # ISO-8601 expected
    dt = pd.to_datetime(dispatch_time, utc=True, errors="coerce")
    if pd.isna(dt):
      return 12
    return int(dt.hour)
  except Exception:
    return 12


def _make_row(features: dict) -> pd.DataFrame:
  return pd.DataFrame([{
    "distance_km": _safe_float(features.get("distance_km"), 0.0),
    "traffic_level": (features.get("traffic_level") or "UNKNOWN"),
    "weather_condition": (features.get("weather_condition") or "UNKNOWN"),
    "supplier_reliability": _safe_float(features.get("supplier_reliability"), 0.5),
    "vehicle_type": (features.get("vehicle_type") or "UNKNOWN"),
    "dispatch_hour": _dispatch_hour(features.get("dispatch_time")),
  }])


def _train_and_save(model_path: str):
  os.makedirs(os.path.dirname(model_path), exist_ok=True)

  n = 1500
  rng = pd.Series(range(n))
  # Synthetic training dataset based on the iteration-3 feature list.
  df = pd.DataFrame({
    "distance_km": (rng % 800) + 10,
    "traffic_level": rng.map(lambda i: ["LOW", "MEDIUM", "HIGH"][i % 3]),
    "weather_condition": rng.map(lambda i: ["CLEAR", "RAIN", "STORM"][i % 3]),
    "supplier_reliability": (rng % 100) / 100.0,
    "vehicle_type": rng.map(lambda i: ["TRUCK", "VAN", "SHIP"][i % 3]),
    "dispatch_hour": rng.map(lambda i: int(i % 24)),
  })

  # Create labels with simple rule-based relationships.
  traffic_factor = df["traffic_level"].map({"LOW": 0.2, "MEDIUM": 0.5, "HIGH": 0.8})
  weather_factor = df["weather_condition"].map({"CLEAR": 0.1, "RAIN": 0.4, "STORM": 0.7})
  distance_factor = (df["distance_km"] / 1000.0).clip(0, 1)
  supplier_factor = (1.0 - df["supplier_reliability"]).clip(0, 1)
  hour_factor = df["dispatch_hour"].map(lambda h: 0.3 if 7 <= h <= 10 or 16 <= h <= 19 else 0.1)

  prob = (0.15 + 0.35 * traffic_factor + 0.25 * weather_factor + 0.15 * distance_factor + 0.25 * supplier_factor + hour_factor)
  prob = prob.clip(0, 0.99)
  y_prob = (prob > 0.5).astype(int)
  y_hours = (prob * 10.0 + distance_factor * 2.0).clip(0, 24)

  cat_features = ["traffic_level", "weather_condition", "vehicle_type"]
  num_features = ["distance_km", "supplier_reliability", "dispatch_hour"]

  preprocessor = ColumnTransformer(
    transformers=[
      ("cat", OneHotEncoder(handle_unknown="ignore"), cat_features),
      ("num", "passthrough", num_features),
    ]
  )

  prob_model = Pipeline(steps=[
    ("prep", preprocessor),
    ("clf", LogisticRegression(max_iter=200))
  ])

  hours_model = Pipeline(steps=[
    ("prep", preprocessor),
    ("reg", RandomForestRegressor(n_estimators=80, random_state=42))
  ])

  prob_model.fit(df, y_prob)
  hours_model.fit(df, y_hours)

  bundle = {"prob_model": prob_model, "hours_model": hours_model}
  with open(model_path, "wb") as f:
    pickle.dump(bundle, f)


@dataclass
class DelayModel:
  prob_model: object
  hours_model: object

  def predict(self, features: dict) -> dict:
    row = _make_row(features)
    try:
      p = float(self.prob_model.predict_proba(row)[0][1])
    except Exception:
      p = 0.5
    try:
      h = float(self.hours_model.predict(row)[0])
    except Exception:
      h = 0.0

    if h < 0:
      h = 0.0
    if p < 0:
      p = 0.0
    if p > 1:
      p = 1.0

    return {
      "delay_probability": round(p, 4),
      "predicted_delay_hours": round(h, 2),
    }


def load_delay_model() -> DelayModel:
  base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
  model_path = os.path.join(base_dir, "models", "delay_model.pkl")

  if not os.path.exists(model_path):
    _train_and_save(model_path)

  try:
    with open(model_path, "rb") as f:
      bundle = pickle.load(f)
    return DelayModel(prob_model=bundle["prob_model"], hours_model=bundle["hours_model"])
  except Exception:
    _train_and_save(model_path)
    with open(model_path, "rb") as f:
      bundle = pickle.load(f)
    return DelayModel(prob_model=bundle["prob_model"], hours_model=bundle["hours_model"])


