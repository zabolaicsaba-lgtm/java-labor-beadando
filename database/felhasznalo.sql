-- Egyszer futtasd le a sajat, mar letezo adatbazisodban.
CREATE TABLE felhasznalo (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    jelszo_hash VARCHAR(100) NOT NULL,
    szerepkor VARCHAR(20) NOT NULL,
    CONSTRAINT uq_felhasznalo_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
