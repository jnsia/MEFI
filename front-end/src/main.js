import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader
import { createVuetify } from "vuetify";
import 'vuetify/dist/vuetify.css';
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";
import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import App from "./App.vue";
import router from "./router";
import './assets/main.css'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'
import { fab } from '@fortawesome/free-brands-svg-icons'
const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
});
const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
library.add(fas)
library.add(far)
library.add(fab)
app.use(router);
app.use(pinia);
app.use(vuetify)
app.component('font-awesome-icon', FontAwesomeIcon)
import { useSettingStore } from './stores/setting';
import { useUserStore } from './stores/user';
const settingStore = useSettingStore()
const userStore = useUserStore()
app.mount("#app");