# VÚB Notifier — Build cez GitHub (DEBUG APK)

Tento build používa `assembleDebug` namiesto `assembleRelease`.

## Prečo to teraz bude fungovať

Predtým sme buildovali `assembleRelease`, ktorý vytvára **nepodpísaný** APK
(`app-release-unsigned.apk`). Android takýto súbor odmietne nainštalovať
s hláškou "interná chyba" — presne to čo sa stalo.

`assembleDebug` automaticky podpisuje APK debug certifikátom, ktorý sa
vygeneruje za behu (Gradle si ho vytvorí sám). Android tento podpis
vždy akceptuje. Žiadne ďalšie kroky, žiadne ručné podpisovanie.

## Postup

1. Vytvor nový **privátny** GitHub repozitár (alebo vyčisti starý)
2. Nahraj **všetky** súbory z tohto priečinka — zachovaj presnú štruktúru:
   ```
   build.gradle
   settings.gradle
   gradle.properties
   app/build.gradle
   app/src/main/AndroidManifest.xml
   app/src/main/java/com/tomiu/vubnotifier/*.kt
   app/src/main/res/...
   .github/workflows/build.yml
   .gitignore
   ```
3. Po nahraní (commit) sa **automaticky** spustí Actions build
4. Choď na záložku **Actions** → posledný (zelený) beh
5. Dole v sekcii **Artifacts** stiahni `VUB-Notifier-APK`
6. Rozbaľ stiahnutý ZIP — vnútri je `app-debug.apk`
7. Presuň `app-debug.apk` na telefón a nainštaluj

Ak build zlyhá, otvor ten konkrétny beh a pošli chybovú hlášku z logu.

## Po inštalácii na telefóne

1. Otvor app **VÚB Notifier**
2. URL servera je prednastavená — uprav ak treba
3. Klikni **Udeliť povolenie** → v zozname zapni VÚB Notifier
4. Hotovo — app teraz počúva LEN notifikácie od `sk.vub.mobilebanking`
