<template>
  <div>{{ message }} {{ name }}</div>
</template>

<script lang="ts">
import {
  defineComponent,
  ref,
  useContext,
  useFetch,
} from "@nuxtjs/composition-api";
export default defineComponent({
  setup() {
    const { app, $firebase } = useContext();

    let name = ref("");
    let message = ref("");

    useFetch(async () => {
      // ログイン済みユーザの取得
      const user = await $firebase.getUser();

      if (!user) {
        // 未ログインのため、ログイン画面に遷移
        app.router?.replace("/signin");
        return;
      }

      const token = await user.getIdToken();
      const res = await fetch("http://localhost:8080/api/hello", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (res.status === 403) {
        app.router?.replace("/signin");
        return;
      }

      name.value = user.displayName!;
      message.value = await res.text();
    });

    const getName = () => {};

    return {
      name,
      message,
    };
  },
});
</script>
