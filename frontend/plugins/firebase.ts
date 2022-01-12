import { initializeApp } from 'firebase/app'
import { getAuth, GoogleAuthProvider, signInWithPopup, User } from 'firebase/auth'
import type { Plugin } from '@nuxt/types'

const firebase: Plugin = ({ $config }, inject) => {

    const firebaseConfig = {
        apiKey: $config.firebaseApiKey,
        authDomain: `springboot-nuxt-auth.firebaseapp.com`,
        projectId: `springboot-nuxt-auth`,
        storageBucket: `springboot-nuxt-auth.appspot.com`,
        messagingSenderId: `768403367330`,
        appId: `1:768403367330:web:8ae1608e50f2b2b8b83efb`,
    }

    // Firebase初期化
    initializeApp(firebaseConfig)

    // ログイン処理
    const login = () => {
        return new Promise<User | false>((resolve, reject) => {
            signInWithPopup(
                getAuth(),
                new GoogleAuthProvider()
            ).then(user => {
                if (user.user) {
                    resolve(user.user)
                } else {
                    resolve(false)
                }
            })
        })
    }

    // ユーザ取得
    const getUser = () => {
        return new Promise<User | false>((resolve, reject) => {
            getAuth().onAuthStateChanged((user) => {
                if (user) {
                    resolve(user)
                } else {
                    resolve(false)
                }
            })
        })
    }

    inject('firebase', { login, getUser })
}

export default firebase