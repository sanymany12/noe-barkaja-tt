Felhasználói történetek
<hr>

Főmenü
<details>
    <summary>Kilépés</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>kilépni a programból</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> A főmenüben vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a "Kilépés" gombra </td></tr>
        <tr><td>THEN</td> <td> Az alkalmazás bezáródik</td></tr>
    </table>
</details>

<details>
    <summary>Játék indítása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Elindítani a játékot</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> A Főmenüben vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a "Játék indítása" gombra</td></tr>
        <tr><td>THEN</td> <td> A játék betölt egy előre meghatározott pályát</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> A betöltés sikeres</td></tr>
        <tr><td>WHEN</td> <td> A játékmenet elindul </td></tr>
        <tr><td>THEN</td> <td> Az idő telése szünetelt módban kezdődik és a játékos megkapja az irányítást</td></tr>
</table>
</details>

<details>
    <summary>Súgó megnyitása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Segítséget kérni a játékhoz</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> A főmenüben vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a Súgó ikonra/gombra</td></tr>
        <tr><td>THEN</td> <td> Megnyílik a súgó felület az információkkal</td></tr>
    </table>
</details>

Játékmenet
<details>
    <summary>Idő telésének szabályozása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>irányítani az idő múlását</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> A játék fut</td></tr>
        <tr><td>WHEN</td> <td> Kiválasztom az "Idő telésének szüneteltetése" opciót</td></tr>
        <tr><td>THEN</td> <td> A játék megáll: a járművek nem mozognak, az épületek leállnak</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> A játék fut/szüneteltetve van</td></tr>
        <tr><td>WHEN</td> <td> Kiválasztom a "Jelentősen gyorsított idősebesség" opciót</td></tr>
        <tr><td>THEN</td> <td> A játékbeli idő és a mozgások maximális sebességgel haladnak</td></tr>
        <tr><td rowspan="3"> 3 </td> <td>GIVEN</td> <td> A játék szüneteltetve van</td></tr>
        <tr><td>WHEN</td> <td> Újra megnyomom a szüneteltetés gombot</td></tr>
        <tr><td>THEN</td> <td> Az idő továbbra is áll (amíg normál/gyorsított sebességet nem választok)</td></tr>
    </table>
</details>

<details>
    <summary>Vissza a menübe</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Visszatérni a főmenübe</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Játékban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Az "Esc" lenyomása után rákattintok a "Vissza a menübe" gombra</td></tr>
        <tr><td>THEN</td> <td> A jelenlegi játékmenet bezárul, és a Főmenü képernyő jelenik meg</td></tr>
    </table>
</details>

<details>
    <summary>Út lehelyezése</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Utakat építeni a pályára</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Játékban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok az "útépítés" ikonra</td></tr>
        <tr><td>THEN</td> <td> Kiválaszthatom a lehelyezni kívánt út típusát vagy eltűntethetem a legördülő menüt</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Kiválasztottam milyen utat szeretnék építeni és üres mezőre építenék, van rá pénzem</td></tr>
        <tr><td>WHEN</td> <td> Lehelyezem az utat megfelelő irányba</td></tr>
        <tr><td>THEN</td> <td> A kiválasztott típusú és irányú út jelenik meg az üres mező helyén</td></tr>
        <tr><td rowspan="3"> 3 </td> <td>GIVEN</td> <td> Kiválaszottam a kívánt utat, a kiválasztott területen fák/erdő található és van elég pénzem eltávolítani</td></tr>
        <tr><td>WHEN</td> <td> Megpróbálom lehelyezni az utat</td></tr>
        <tr><td>THEN</td> <td> A "Fa/erdő eltávolítása" megtörténik, majd az út megépül</td></tr>
        <tr><td rowspan="3"> 4 </td> <td>GIVEN</td> <td> Kiválasztottam a kívánt utat</td></tr>
        <tr><td>WHEN</td> <td> Olyan területre kattintok, ami víz, vagy ahol már egy épület áll</td></tr>
        <tr><td>THEN</td> <td> Hibaüzenet jelenik meg, és az út nem épül meg</td></tr>
    </table>
</details>

<details>
    <summary>Híd lehelyezése</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Hidat építeni a víz felett</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Kiválasztottam egy híd típust</td></tr>
        <tr><td>WHEN</td> <td> Két megfelelő, kiválasztott vízparti pont között húzom az egeret</td></tr>
        <tr><td>THEN</td> <td> A kiválasztott híd megépül a víz felett</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Kiválaszottam egy híd típust</td></tr>
        <tr><td>WHEN</td> <td> Szárazföld felett vagy átlósan próbálom lehelyezni</td></tr>
        <tr><td>THEN</td> <td> Hibaüzenet jelenik meg, kattintáskor nem épül meg</td></tr>
    </table>
</details>

<details>
    <summary>Megálló lehelyezése</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Megállókat kijelölni az utasok/nyersanyagok/állatok számára</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Kiválaszottam, hogy megállót akarok építeni és van elég pénzem</td></tr>
        <tr><td>WHEN</td> <td> Egy épület mellé kattintok</td></tr>
        <tr><td>THEN</td> <td> A megálló lehelyezésre kerül az épület mellett, ha erdőre került, extra költség felszámolása</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Megálló építése módban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Vízre/épületre/épülettel nem szomszédos helyre tenném</td></tr>
        <tr><td>THEN</td> <td> Hibaüzenetet kapok, a megálló nem épül meg</td></tr>
    </table>
</details>

<details>
    <summary>Saját építésű struktúra rombolása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Lerombolni az általam épített elemeket</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Rombolás módban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok egy általam épített útra, hídra vagy megállóra</td></tr>
        <tr><td>THEN</td> <td> A struktúra eltűnik a térképről, visszakapom a költség kis részét</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Rombolás módban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok egy olyan épületre/természeti elemre/útra, ami a pálya alapértelmezett része</td></tr>
        <tr><td>THEN</td> <td> A rombolás nem történik meg, hibaüzenetet kapok</td></tr>
    </table>
</details>

<details>
    <summary>Épület / Megálló infólapjának megnyitása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Információkat látni az épületekről és megállókról</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Játékban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok egy városi épületre</td></tr>
        <tr><td>THEN</td> <td> Megnyílik az épület infólapja (pl. termelt áru, kapacitás)</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Jétékban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok egy általam épített megállóra</td></tr>
        <tr><td>THEN</td> <td> Megnyílik a megálló infólapja ???</td></tr>
    </table>
</details>

<details>
    <summary>Jármű vásárlása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Új járművet venni</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Nyitva van egy megálló infólapja és van elegendő pénzem</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a "Jármű vásárlása" opcióra</td></tr>
        <tr><td>THEN</td> <td> A jármű megvásárlásra kerül, a pénzem csökken, és a jármű megjelenik a megállóban</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Nyitva van egy megálló infólapja, de foglalja egy jármű</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a "Jármű vásárlása" opcióra</td></tr>
        <tr><td>THEN</td> <td> A vásárlás sikertelen, figyelmeztetést kapok az állomásozó/lerakodó járműről</td></tr>
    </table>
</details>

<details>
    <summary>Jármű infólapja és útvonal kiadása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Kezelni a járműveimet és útvonalat adni nekik</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Van egy járművem a pályán</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a járműre</td></tr>
        <tr><td>THEN</td> <td> Megnyílik a jármű infólapja</td></tr>
        <tr><td rowspan="3"> 2 </td> <td>GIVEN</td> <td> Nyitva van a jármű infólapja</td></tr>
        <tr><td>WHEN</td> <td> Kiválasztom az "Útvonal kiadása" opciót és kijelölök érvényes megállókat (pl. A - B - C - A)</td></tr>
        <tr><td>THEN</td> <td> A jármű megkapja az útvonalat és elindul a célok felé</td></tr>
        <tr><td rowspan="3"> 3 </td> <td>GIVEN</td> <td> Útvonal kiadása folyamatban van</td></tr>
        <tr><td>WHEN</td> <td> Olyan megállót jelölök ki, amihez nem vezet érvényes útkapcsolat az adott jármű számára</td></tr>
        <tr><td>THEN</td> <td> Hibaüzenetet kapok, a jármű nem indul el</td></tr>
    </table>
</details>

<details>
    <summary>Jármű eladása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Eladni egy meglévő járművemet</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> Nyitva van a jármű infólapja, és a jármű egy megállóban van</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a "Jármű eladása" gombra</td></tr>
        <tr><td>THEN</td> <td> A jármű eltűnik a pályáról, az útvonala törlődik, és visszakapom az árának egy részét</td></tr>
    </table>
</details>

<details>
    <summary>Súgó megnyitása</summary>
    <table>
        <tr><td colspan="2"> AS A </td> <td>Játékos</td></tr>
        <tr><td colspan="2"> I WANT TO</td> <td>Segítséget kérni a játékhoz</td></tr>
        <tr><td rowspan="3"> 1 </td> <td>GIVEN</td> <td> A játékban vagyok</td></tr>
        <tr><td>WHEN</td> <td> Rákattintok a Súgó ikonra/gombra</td></tr>
        <tr><td>THEN</td> <td> Megnyílik a súgó felület az információkkal</td></tr>
    </table>
</details>
