import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyChVn0mikKWplM7SgZzvytSSFoXGBPufBk",
  authDomain: "trip-planner-5b77a.firebaseapp.com",
  projectId: "trip-planner-5b77a",
  storageBucket: "trip-planner-5b77a.firebasestorage.app",
  messagingSenderId: "1032339859581",
  appId: "1:1032339859581:web:2c2572e7b0a93f14bd2914" // Placeholder web ID
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);
export const googleProvider = new GoogleAuthProvider();
