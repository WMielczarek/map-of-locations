<template>
    <v-app>
     
    <navigateBar/>

    <!-- Sizes your content based upon application components -->
    <v-content>

      <!-- Provides the application the proper gutter -->
      <v-container fluid>
        <router-view><Home/></router-view>
      </v-container>
    </v-content>
    </v-app>
</template>

<script>

import Home from "@/components/Home";
import NavigateBar from "./components/NavigateBar"

export default {
  name: 'App',
  components: {
    Home,
    NavigateBar
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0px;
  padding: 0px;
}

</style>
