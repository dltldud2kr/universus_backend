import pandas as pd
from sqlalchemy import create_engine

db_url = 'oracle+cx_oracle://uv_user:qwer7726@localhost:1521/orcl'
engine = create_engine(db_url)