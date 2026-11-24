#!/bin/sh

# Read-only checks; the shell's Java version may differ from Tomcat's.
tomcat_home=${TOMCAT_HOME:-/opt/tomcat}
problem=0

printf '%s\n' '=== Java a bejelentkezett felhasznalo kornyezeteben ==='
if command -v java >/dev/null 2>&1; then
    if ! java -version 2>&1; then
        printf '%s\n' 'HIBA: a java parancs nem futtathato.'
        problem=1
    fi
else
    printf '%s\n' 'HIBA: a java parancs nem talalhato.'
    problem=1
fi

printf '\n%s\n' '=== Tomcat telepites ==='
printf 'Tomcat konyvtar: %s\n' "$tomcat_home"
if [ -r "$tomcat_home/RELEASE-NOTES" ]; then
    sed -n '1,12p' "$tomcat_home/RELEASE-NOTES"
else
    printf '%s\n' 'FIGYELEM: a Tomcat verzioja nem olvashato a RELEASE-NOTES fajlbol.'
fi

if [ -d "$tomcat_home/webapps" ]; then
    printf '%s\n' 'A webapps konyvtar letezik.'
    if [ -w "$tomcat_home/webapps" ]; then
        printf '%s\n' 'A jelenlegi felhasznalo irhatja a webapps konyvtarat.'
    else
        printf '%s\n' 'HIBA: a jelenlegi felhasznalo nem irhatja a webapps konyvtarat.'
        problem=1
    fi
else
    printf '%s\n' 'HIBA: a webapps konyvtar nem talalhato.'
    problem=1
fi

printf '\n%s\n' '=== MySQL kliens ==='
if command -v mysql >/dev/null 2>&1; then
    mysql --version || {
        printf '%s\n' 'HIBA: a mysql parancs nem futtathato.'
        problem=1
    }
else
    printf '%s\n' 'FIGYELEM: a mysql kliens nem talalhato; az importhoz masik kliens szukseges.'
fi

printf '\n%s\n' 'FONTOS: ellenorizd kulon a Tomcat folyamat Java-verziojat, a Tomcat kompatibilitasat,'
printf '%s\n' 'az adatbazis-kapcsolatot, a biztonsagos HTTPS-t es az alkalmazas tenyleges indulasat.'
exit "$problem"
