import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify';
import '@babel/polyfill';
import VueResource from 'vue-resource';
import NewProject from './components/NewProject.vue';
import MainMap from './components/MainMap.vue';
import VueRouter from 'vue-router';
import {routes} from './routes';

const router = new VueRouter({
  routes
});

Vue.use(VueRouter);
Vue.use(VueResource);
Vue.config.productionTip = false;
Vue.component('new-project-ner', NewProject);
Vue.component('main-map', MainMap);


new Vue({
  vuetify,
  router,
  render: h => h(App)
}).$mount('#app')
