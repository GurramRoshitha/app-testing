import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  signInWithPopup,
  updateProfile
} from "firebase/auth";
import { auth, googleProvider, db } from "./firebase";
import { doc, setDoc, getDoc } from "firebase/firestore";

class AuthService {
  async signIn(email, password) {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      return { isSuccess: true, user: userCredential.user };
    } catch (error) {
      return { isSuccess: false, error };
    }
  }

  async signUp(email, password, name) {
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      await updateProfile(userCredential.user, { displayName: name });

      // Initialize profile in Firestore
      await setDoc(doc(db, "profiles", userCredential.user.uid), {
        fullName: name,
        email: email,
        isRegistered: true
      });

      return { isSuccess: true, user: userCredential.user };
    } catch (error) {
      return { isSuccess: false, error };
    }
  }

  async signInWithGoogle() {
    try {
      const result = await signInWithPopup(auth, googleProvider);
      const user = result.user;

      // Save profile if new
      const docRef = doc(db, "profiles", user.uid);
      const docSnap = await getDoc(docRef);

      if (!docSnap.exists()) {
        await setDoc(docRef, {
          fullName: user.displayName || "Voyage Explorer",
          email: user.email,
          isRegistered: true
        });
      }

      return { isSuccess: true, user };
    } catch (error) {
      return { isSuccess: false, error };
    }
  }

  async logout() {
    try {
      await signOut(auth);
      return { isSuccess: true };
    } catch (error) {
      return { isSuccess: false, error };
    }
  }

  getCurrentUser() {
    return auth.currentUser || { displayName: "Voyage Explorer", email: "explorer@voyage.ai", uid: "guest_user" };
  }

  getUserId() {
    return auth.currentUser?.uid;
  }
}

export default new AuthService();
