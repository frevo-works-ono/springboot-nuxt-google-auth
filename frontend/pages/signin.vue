<template>
  <button type="button" @click="login">Google認証</button>
</template>

<script lang="ts">
import { defineComponent, useContext } from "@nuxtjs/composition-api";

export default defineComponent({
  setup() {
    const { app, $firebase } = useContext();

    const login = async () => {
      const user = await $firebase.login();
      if (!user) {
        // 認証エラー
        return;
      }

      app.router?.replace("/");
    };

    return {
      login,
    };
  },
});
</script>
