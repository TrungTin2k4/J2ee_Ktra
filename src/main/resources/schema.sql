SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS patient_role;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS doctor;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS account_role;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `role` (
    role_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_name (name)
);

CREATE TABLE patient (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_username (username),
    UNIQUE KEY uk_patient_email (email)
);

CREATE TABLE patient_role (
    patient_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (patient_id, role_id),
    CONSTRAINT fk_patient_role_patient FOREIGN KEY (patient_id) REFERENCES patient(id),
    CONSTRAINT fk_patient_role_role FOREIGN KEY (role_id) REFERENCES `role`(role_id)
);

CREATE TABLE department (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_department_name (name)
);

CREATE TABLE doctor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    specialty VARCHAR(255) NOT NULL,
    department_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_doctor_department (department_id),
    CONSTRAINT fk_doctor_department FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE appointment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_appointment_booking (patient_id, doctor_id, appointment_date),
    KEY idx_appointment_doctor (doctor_id),
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);
