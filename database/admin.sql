-- ADMIN szerepkor kiosztasa egy mar regisztralt felhasznalonak.
-- 1. Regisztralj a /regisztracio oldalon (peldaul "admin" felhasznalonevvel).
-- 2. Futtasd le ezt a parancsot a sajat adatbazisodban.
-- A jelszo sosem kerul a forraskodba: a hash a regisztracio soran keletkezik.
UPDATE felhasznalo SET szerepkor = 'ADMIN' WHERE username = 'admin';

-- Ellenorzes:
SELECT username, szerepkor FROM felhasznalo;