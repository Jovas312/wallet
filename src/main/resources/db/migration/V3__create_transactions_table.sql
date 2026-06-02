CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              source_wallet_id UUID,
                              destination_wallet_id UUID,
                              amount DECIMAL(19, 4) NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              description VARCHAR(255),
                              created_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_transactions_source_wallet FOREIGN KEY (source_wallet_id) REFERENCES wallets(id),
                              CONSTRAINT fk_transactions_destination_wallet FOREIGN KEY (destination_wallet_id) REFERENCES wallets(id)
);