from flask import Flask, jsonify, request
from service.model_loader import load_delay_model

app = Flask(__name__)
delay_model = load_delay_model()


@app.route("/health", methods=["GET"])
def health():
  return jsonify({"status": "UP", "service": "python-ml-service"}), 200


@app.route("/predict-delay", methods=["POST"])
def predict_delay():
  payload = request.get_json(force=True) or {}
  result = delay_model.predict(payload)
  return jsonify(result), 200


if __name__ == "__main__":
  app.run(host="0.0.0.0", port=5000)

