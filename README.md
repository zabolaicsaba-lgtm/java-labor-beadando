# Szélerőművek Magyarországon

Spring Boot alkalmazás a megadott magyarországi szélerőmű-adatállomány alapján.
A kezdőoldal a [HTML5 UP Stellar](https://html5up.net/stellar) sablonjának
módosított változatát használja ([CC BY 3.0](https://creativecommons.org/licenses/by/3.0/)).

## Adatbázis előkészítése

A `database/szeleromuvek.sql` a mellékelt `megye.txt`, `helyszin.txt` és
`torony.txt` adatokat tartalmazza. A megyenevek és régiók végén lévő felesleges
szóközöket eltávolítottuk. A script a meglévő adatbázisba hozza létre a három
táblát; nem hoz létre adatbázist, és nem töröl meglévő adatokat.

Csak olyan adatbázisba importáld, amelyben még nincs `megye`, `helyszin` és
`torony` tábla. A script nem ismételhető: hiba esetén az előzőleg létrejött
táblák és adatok megmaradhatnak, ezért újrafuttatás előtt ellenőrizd az állapotot.

A Linux szerveren például a SQL-fájl feltöltése után, a fájl könyvtárából:

```bash
mysql --default-character-set=utf8mb4 -u SAJAT_FELHASZNALO -p SAJAT_ADATBAZIS < szeleromuvek.sql
```

Az adatbázis nevét és felhasználóját a saját adataidra kell cserélni; a jelszót
a MySQL-kliens kérdezi be. A szerverhez a kiadott útmutató alapján SSH-val lehet
csatlakozni, távolról grafikus kliens esetén SSH-alagút szükséges.

## Adatbázis-kapcsolat

Alapértelmezésben a kezdőoldal adatbázis nélkül fut. Az adatbázis használatához
aktiváld a `mysql` Spring-profilt, és add meg az alábbi környezeti változókat:

| Változó | Jelentés |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `mysql` |
| `MYSQL_DATABASE` | A meglévő adatbázis neve |
| `MYSQL_USER` | Az adatbázis felhasználója |
| `MYSQL_PASSWORD` | Az adatbázis jelszava |
| `MYSQL_HOST` | Adatbázis-kiszolgáló, alapértelmezés: `localhost` |
| `MYSQL_PORT` | MySQL-port, alapértelmezés: `3306` |

A jelszót ne írd be a forráskódba, és ne commitold. Külső Tomcat esetén a
beállításoknak a Tomcat folyamatában kell rendelkezésre állniuk; a megosztott
szerver üzemeltetőjével egyeztesd, hogyan adhatók meg biztonságosan.
Az alkalmazás jelenleg még nem jeleníti meg a táblák adatait; ez a következő
fejlesztési lépésekben készül el.

## WAR-csomag

Windows alatt a `.\mvnw.cmd package`, Linux alatt a `./mvnw package` készíti
el a WAR-fájlt a `target` könyvtárban. A kiírás szerint a feltöltött fájl neve
legyen a saját, kisbetűs Neptun-kódod, például `abc123.war`. A telepítés után
az alkalmazás a megfelelő `http://rivendell.nje.hu:9443/abc123/` útvonalon
érhető el. A szerveren telepített Java- és Tomcat-verzió kompatibilitását
telepítés előtt ellenőrizni kell.
