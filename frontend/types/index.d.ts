import { User } from 'firebase/auth';

interface Firebase {
    login: () => Promise<User>
    getUser: () => Promise<User>
}

declare module 'vue/types/vue' {
    interface Vue {
        readonly $firebase: Firebase
    }
}

declare module '@nuxt/types' {
    interface NuxtAppOptions {
        readonly $firebase: Firebase
    }
}

declare module '@nuxt/types' {
    interface Context {
        readonly $firebase: Firebase
    }
}

declare module 'vuex' {
    interface Store<S> {
        readonly $firebase: Firebase
    }
}