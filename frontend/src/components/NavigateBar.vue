<template>

    <div>
      <v-app-bar
        color="black"
        dense
        dark
      >

        <v-toolbar-title><router-link class="routerLink" to="/">Literary-map</router-link></v-toolbar-title>
       
        <router-link class="routerLink" to="/newProject">Stwórz nowy projekt</router-link>
        <router-link class="routerLink" to="/openProject">Otwórz projekt</router-link>
        
        <v-spacer></v-spacer>

        <router-link class="routerLink" to="/about">O projekcie</router-link>
      </v-app-bar>
    </div>

</template>

<script>
    export default {
        name: "NavigateBar",
        data: () => ({
            drawer: false,
        }),
    }
</script>

<style scoped>
   .routerLink{
     text-decoration: none;
     color: white;
     padding: 1em;
    }

    
</style>
