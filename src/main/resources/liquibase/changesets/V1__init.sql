CREATE TABLE "account"(
                          "id" bigserial NOT NULL,
                          "number" UUID NOT NULL,
                          "balance" BIGINT NOT NULL DEFAULT '1000'
);
ALTER TABLE
    "account" ADD PRIMARY KEY("id");
ALTER TABLE
    "account" ADD CONSTRAINT "account_number_unique" UNIQUE("number");
COMMENT
    ON COLUMN
    "account"."balance" IS 'Сумма на счете по умолчанию у всех пользователей.';
CREATE TABLE "client"(
                         "id" bigserial NOT NULL,
                         "account_id" BIGINT NOT NULL,
                         "username" VARCHAR(255) NOT NULL,
                         "name" VARCHAR(255) NULL,
                         "surname" VARCHAR(255) NULL,
                         "patronymic" VARCHAR(255) NULL,
                         "birthdate" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
                         "password" TEXT NOT NULL
);
ALTER TABLE
    "client" ADD PRIMARY KEY("id");
ALTER TABLE
    "client" ADD CONSTRAINT "client_account_id_unique" UNIQUE("account_id");
ALTER TABLE
    "client" ADD CONSTRAINT "client_username_unique" UNIQUE("username");
CREATE TABLE "email"(
                        "id" bigserial NOT NULL,
                        "email" VARCHAR(255) NOT NULL,
                        "description" VARCHAR(255) NULL,
                        "client_id" BIGINT NOT NULL
);
ALTER TABLE
    "email" ADD PRIMARY KEY("id");
ALTER TABLE
    "email" ADD CONSTRAINT "email_email_unique" UNIQUE("email");
CREATE TABLE "phone_number"(
                               "id" bigserial NOT NULL,
                               "number" VARCHAR(255) NOT NULL,
                               "description" VARCHAR(255) NULL,
                               "client_id" BIGINT NOT NULL
);
ALTER TABLE
    "phone_number" ADD PRIMARY KEY("id");
ALTER TABLE
    "phone_number" ADD CONSTRAINT "phone_number_number_unique" UNIQUE("number");
ALTER TABLE
    "phone_number" ADD CONSTRAINT "phone_number_client_id_foreign" FOREIGN KEY("client_id") REFERENCES "client"("id") ON DELETE CASCADE;
ALTER TABLE
    "client" ADD CONSTRAINT "client_account_id_foreign" FOREIGN KEY("account_id") REFERENCES "account"("id") ON DELETE CASCADE;
ALTER TABLE
    "email" ADD CONSTRAINT "email_client_id_foreign" FOREIGN KEY("client_id") REFERENCES "client"("id") ON DELETE CASCADE;
