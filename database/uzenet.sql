-- Egyszer futtasd le a mar letezo sajat adatbazisodban.
CREATE TABLE uzenet (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nev VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    szoveg VARCHAR(4000) NOT NULL,
    kuldes_ideje DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
