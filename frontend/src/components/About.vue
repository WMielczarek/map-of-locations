<template>
       <v-container>
            <v-card
                class="mx-auto"
                outlined
            >
              
                <h1>Autorzy:</h1>
                <p>Michał Bańka, nr indeksu: 235051</p>
                <p>Przemysław Jedlikowski, nr indeksu: 234927</p>
                <p>Witold Marciniak, nr indeksu: 226194</p>
                <p>Wojciech Mielczarek, nr indeksu: 218393</p>
                <p>Marcin Pawluś, nr indeksu: 235555</p>
                <h1>O projekcie:</h1>
                <p>Projekt ten powstał w ramach przemiotu "Zastosowania informatyki w gospodarce" prowadzonego przez dr inż. Tomasza Walkowiaka. Realizuje on temat (2.) "Mapa literacka (geolokalizacja na podstawie danych z Linera) dla korpusów (zmienność w czasie, np. rozdziały) (PL)". Projekt został stworzony w języku Java z użyciem Springa oraz frameworku Vue. Dane z Linera są przechowywane w nierelacyjne bazie danych MongoDB. Wykorzystano również narzędzie Linera służące do analizy tekstu lub tekstów oraz uzyskania nazw własnych lokalizacji znajdujących się w tekście, a także Nominatim do uzyskania współrzędnych w postaci pliku JSON na podstawie nazwy własnej.</p>
                <p>Projekt ma za zadanie ułatwić anlizę tekstów, a jego głównymi użytkownikami będą naukowcy nauk humanistycznych. Pozwala on na tworzenie nowych projektów (analiz tekstów) oraz przechowywanie ich do późniejszych analiz przez użytkownika, wizualizuje przetworzone informacje poprzez naniesienie punktów na mapę, pozwala na modyfikację przetworzonych danych przez użytkownika oraz pozwala użytkownikowi na rozszerzenie istniejącego słownika poprzez dodanie swoich zwrótów. Przetwarzanie wprowadzonych danych może trwać kilka minut lub kilka godzin, a cały proces nie jest responsywny. Przykładowe użycie aplikacji powinno wyglądać następująco: najpierw następuje wgranie korpusu tekstów, potem użytkownik ma możliwość poszerzenia słownika o własne zwroty, kolejno następuje przetwarzanie danych, po którym istnieje możliwość przetwarzania parametrów przez użytkownika, a na koniec wizualizacja i naniesienie wyników na mapę.</p>
        
            </v-card>
    </v-container>

</template>

<script>
    export default {
        name: "About",
    }
</script>

<style scoped>
  
</style>