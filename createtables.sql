CREATE TABLE IF NOT EXISTS jobs (
    id varchar(36) PRIMARY KEY,
    title varchar(100) NOT NULL,
    companyId integer REFERENCES companies(id)
);

CREATE TABLE IF NOT EXISTS companies (
     id varchar(36) PRIMARY KEY,
     name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
     id varchar(36) PRIMARY KEY,
     firstName varChar(50),
     lastName varChar(50),
     email varChar(100) NOT NULL,
     password varChar(12) NOT NULL
);

CREATE TABLE IF NOT EXISTS applications (
    userId varchar(36) REFERENCES users(id),
    jobId integer REFERENCES jobs(id),
    date date NOT NULL,
    status ENUM('waiting', 'interviewing', 'rejected', 'offered', 'accepted') NOT NULL
);