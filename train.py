from dotenv import load_dotenv
import os
import pandas as pd
import json
import joblib
from sqlalchemy import create_engine
from sklearn.preprocessing import StandardScaler, MultiLabelBinarizer
from sklearn.neural_network import MLPRegressor
from sklearn.pipeline import Pipeline

load_dotenv()

# DB 접속 정보
USERNAME = os.getenv("DB_USERNAME")
PASSWORD = os.getenv("DB_PASSWORD")
HOST     = os.getenv("DB_MLP_HOST")
PORT     = os.getenv("DB_PORT")
DATABASE = os.getenv("DB_MLP_NAME")

# 1. 데이터 로드
engine = create_engine(f"mysql+pymysql://{USERNAME}:{PASSWORD}@{HOST}:{PORT}/{DATABASE}")
df = pd.read_sql("SELECT body_number, stage, model_type, option_list, start_at, end_at FROM mlp_learning", engine)
engine.dispose()

# 2. 소요 시간 계산
df["start_at"] = pd.to_datetime(df["start_at"])
df["end_at"]   = pd.to_datetime(df["end_at"])
df["process_duration_h"] = (df["end_at"] - df["start_at"]).dt.total_seconds() / 3600

# 3. 운송 지연 시간 계산
df = df.sort_values(["body_number", "stage"])
df["prev_end"] = df.groupby("body_number")["end_at"].shift(1)
df["transport_delay_h"] = (df["start_at"] - df["prev_end"]).dt.total_seconds() / 3600
df["transport_delay_h"] = df["transport_delay_h"].fillna(0)

# 4. 옵션 파싱 및 원-핫 인코딩
df["opts"] = df["option_list"].apply(lambda x: json.loads(x) if isinstance(x, str) else [])
mlb = MultiLabelBinarizer(classes=[1, 2, 3, 4, 5])
opt_mat = mlb.fit_transform(df["opts"])
opt_df = pd.DataFrame(opt_mat, columns=[f"opt_{i}" for i in mlb.classes_], index=df.index)
df = pd.concat([df, opt_df], axis=1)

# 5. 모델타입 및 공정단계 원-핫 인코딩
df = pd.get_dummies(df, columns=["model_type", "stage"], drop_first=False)

# 6. 4단계 옵션만 반영
df["stage_4_flag"] = df.get("stage_4", 0)
for i in range(1, 6):
    df[f"opt_{i}_only4"] = df[f"opt_{i}"] * df["stage_4_flag"]

# 7. 피처 설정
feature_cols = (
        [f"model_type_{m}" for m in ["ICE", "HEV", "EV"]] +
        [f"stage_{s}" for s in range(1, 6)] +
        [f"opt_{i}_only4" for i in range(1, 6)] +
        ["transport_delay_h"]
)

# 누락 컬럼 처리 (예: 특정 모델/스테이지 없는 경우)
for col in feature_cols:
    if col not in df.columns:
        df[col] = 0.0

X = df[feature_cols]
y = df["process_duration_h"]

# 8. 모델 구성 및 학습
model = Pipeline([
    ("scaler", StandardScaler()),
    ("mlp", MLPRegressor(hidden_layer_sizes=(100, 100), max_iter=500, random_state=42))
])
model.fit(X, y)

# 9. 모델 저장
joblib.dump(model, "mlp_single_custom.pkl")

# 10. 학습 결과 출력
y_pred = model.predict(X)
print(f"학습 완료: 실제 평균 = {y.mean():.3f}h, 예측 평균 = {y_pred.mean():.3f}h")
