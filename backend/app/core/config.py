"""Application configuration loaded from environment variables."""
from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    app_name: str = "FIGHT API"
    app_env: str = "development"
    debug: bool = True

    db_host: str = "localhost"
    db_port: int = 3306
    db_name: str = "fight_db"
    db_user: str = "root"
    db_password: str = ""

    # Optional full connection string. When set it takes precedence over the
    # individual DB_* fields, which keeps local dev (e.g. SQLite) flexible.
    database_url_override: str | None = Field(default=None, alias="DATABASE_URL")

    secret_key: str = "change-me-in-production"
    access_token_expire_minutes: int = 60
    algorithm: str = "HS256"

    @property
    def database_url(self) -> str:
        if self.database_url_override:
            return self.database_url_override
        return (
            f"mysql+pymysql://{self.db_user}:{self.db_password}"
            f"@{self.db_host}:{self.db_port}/{self.db_name}?charset=utf8mb4"
        )


settings = Settings()
