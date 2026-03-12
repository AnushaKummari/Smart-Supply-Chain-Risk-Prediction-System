# SSRP Database Schema
Smart Supply Chain Risk Prediction System

Database: PostgreSQL

---

# 1. USERS TABLE

Stores application users.

Table: users

Columns

id (PK)  
name  
email  
password  
role  
created_at

Example

Admin  
Operations Manager  
Supply Chain Analyst

---

# 2. SUPPLIERS TABLE

Stores supplier information.

Table: suppliers

Columns

id (PK)  
supplier_name  
contact_email  
contact_phone  
average_lead_time  
on_time_delivery_rate  
defect_rate  
cancellation_rate  
reliability_score  
created_at

Purpose

Supplier performance analysis.

---

# 3. SHIPMENTS TABLE

Stores shipment details.

Table: shipments

Columns

id (PK)  
supplier_id (FK)  
source_location  
destination_location  
distance_km  
vehicle_type  
dispatch_time  
expected_delivery_time  
actual_delivery_time  
weather_condition  
traffic_level  
shipment_status  
predicted_delay_hours  
delay_probability  
risk_score  
created_at

Purpose

Shipment monitoring and prediction.

---

# 4. INVENTORY TABLE

Tracks warehouse inventory.

Table: inventory

Columns

id (PK)  
product_name  
warehouse_name  
current_stock  
reorder_level  
avg_daily_usage  
incoming_stock  
supplier_id (FK)  
shortage_risk_score  
created_at

Purpose

Predict stock shortages.

---

# 5. ALERTS TABLE

Stores system alerts.

Table: alerts

Columns

id (PK)  
alert_type  
alert_message  
severity  
shipment_id (FK)  
supplier_id (FK)  
inventory_id (FK)  
created_at

Severity Levels

LOW  
MEDIUM  
HIGH  
CRITICAL

Purpose

Notify system users of risks.

---

# 6. PREDICTIONS TABLE

Stores ML prediction outputs.

Table: predictions

Columns

id (PK)  
shipment_id (FK)  
delay_probability  
predicted_delay_hours  
prediction_time

Purpose

Track ML prediction history.

---

# 7. ANALYTICS TABLE

Stores aggregated analytics metrics.

Table: analytics

Columns

id (PK)  
metric_name  
metric_value  
calculation_date

Examples

On-Time Delivery Rate  
Average Delay Time  
High Risk Shipment Count

---

# 8. ENTITY RELATIONSHIPS

Relationships

Supplier → Shipments

One supplier can have many shipments.

Supplier → Inventory

One supplier supplies multiple products.

Shipment → Alerts

Shipment can generate multiple alerts.

Shipment → Predictions

Shipment can have multiple predictions.

---

# 9. ER DIAGRAM (Logical)

Users
  |
  | manages
  |
Shipments ---- Suppliers
     |
     |
Predictions
     |
     |
Alerts

Inventory ---- Suppliers

---

# 10. Database Indexing

Recommended indexes

shipment_id index  
supplier_id index  
dispatch_time index  
risk_score index

Benefits

- faster queries
- faster dashboard loading
- optimized analytics

---

# 11. Data Retention Strategy

Shipment data retention

2–5 years

Prediction logs

1 year

Alert history

6 months

---

# 12. Migration Strategy

Use:

Spring Boot Flyway or Liquibase

Benefits

- version controlled schema
- safe database migrations