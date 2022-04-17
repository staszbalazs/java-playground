CREATE TABLE person (
    id bigint PRIMARY KEY,
    first_name varchar NOT NULL,
    last_name varchar NOT NULL
);

INSERT INTO person (id, first_name, last_name) VALUES (1, 'John', 'Doe');
INSERT INTO person (id, first_name, last_name) VALUES (2, 'Bill', 'Gates');
INSERT INTO person (id, first_name, last_name) VALUES (3, 'Ano', 'Nymous');
INSERT INTO person (id, first_name, last_name) VALUES (4, 'Max', 'Min');