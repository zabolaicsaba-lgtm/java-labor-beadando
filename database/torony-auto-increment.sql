-- Csak a korabban importalt, AUTO_INCREMENT nelkuli torony tablaba.
-- A letezo sorok es azonositoik megmaradnak.
ALTER TABLE torony MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT;
