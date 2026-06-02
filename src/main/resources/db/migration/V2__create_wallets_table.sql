CREATE TABLE wallets (
                         id UUID PRIMARY KEY,
                         user_id UUID NOT NULL UNIQUE,
                         balance DECIMAL(19, 4) NOT NULL,
                         version BIGINT NOT NULL,
                         updated_at TIMESTAMP NOT NULL,

                         CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);