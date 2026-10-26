import {
  collection,
  addDoc,
  getDocs,
  getDoc,
  deleteDoc,
  updateDoc,
  doc,
  query,
  where,
  setDoc,
  onSnapshot
} from "firebase/firestore";
import { db, auth } from "./firebase";

class TripService {
  subscribeToTrips(onTripsChanged) {
    const userId = auth.currentUser?.uid || "guest_user_123";
    const q = query(
      collection(db, "trips"),
      where("user_id", "==", userId)
    );

    return onSnapshot(q, (snapshot) => {
      const trips = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
      trips.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));

      localStorage.setItem('voyage_trips', JSON.stringify(trips));
      onTripsChanged(trips);
    }, (error) => {
      console.warn("Firestore real-time subscription error:", error);
    });
  }
  async createTrip(trip) {
    try {
      const userId = auth.currentUser?.uid || "guest_user_123";
      const tripId = trip.id || `trip_${Date.now()}`;
      const tripData = {
        ...trip,
        id: tripId,
        user_id: userId,
        createdAt: Date.now()
      };

      try {
        await setDoc(doc(db, "trips", tripId), tripData);
      } catch (fbErr) {
        console.warn("Firebase save skipped/offline, saving locally:", fbErr);
      }

      const existing = JSON.parse(localStorage.getItem('voyage_trips') || '[]');
      const filtered = existing.filter(t => t.id !== tripId);
      filtered.unshift(tripData);
      localStorage.setItem('voyage_trips', JSON.stringify(filtered));

      return { isSuccess: true, id: tripId };
    } catch (error) {
      console.error("Error creating trip:", error);
      return { isSuccess: false, error: error.message };
    }
  }

  async getTrips() {
    try {
      let trips = [];
      const userId = auth.currentUser?.uid || "guest_user_123";

      try {
        const q = query(
          collection(db, "trips"),
          where("user_id", "==", userId)
        );
        const querySnapshot = await getDocs(q);
        trips = querySnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        trips.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));
      } catch (e) {
        console.warn("Firestore fetch error, fetching locally:", e);
      }

      const localTrips = JSON.parse(localStorage.getItem('voyage_trips') || '[]');
      const combined = [...trips, ...localTrips.filter(lt => !trips.some(t => t.id === lt.id))];

      return { isSuccess: true, trips: combined };
    } catch (error) {
      return { isSuccess: false, error: error.message };
    }
  }

  async getTripById(tripId) {
    try {
      if (auth.currentUser) {
        try {
          const docRef = doc(db, "trips", tripId);
          const docSnap = await getDoc(docRef);
          if (docSnap.exists()) {
            return { isSuccess: true, trip: { id: docSnap.id, ...docSnap.data() } };
          }
        } catch (e) {}
      }

      const localTrips = JSON.parse(localStorage.getItem('voyage_trips') || '[]');
      const trip = localTrips.find(t => t.id === tripId);
      if (trip) return { isSuccess: true, trip };

      return { isSuccess: false, error: "Trip not found" };
    } catch (error) {
      return { isSuccess: false, error: error.message };
    }
  }

  async deleteTrip(tripId) {
    try {
      if (auth.currentUser) {
        try {
          await deleteDoc(doc(db, "trips", tripId));
        } catch (e) {}
      }

      const localTrips = JSON.parse(localStorage.getItem('voyage_trips') || '[]');
      const updated = localTrips.filter(t => t.id !== tripId);
      localStorage.setItem('voyage_trips', JSON.stringify(updated));

      return { isSuccess: true };
    } catch (error) {
      return { isSuccess: false, error: error.message };
    }
  }

  async updateTripStatus(tripId, newStatus) {
    try {
      if (auth.currentUser) {
        try {
          await updateDoc(doc(db, "trips", tripId), { status: newStatus });
        } catch (e) {}
      }

      const localTrips = JSON.parse(localStorage.getItem('voyage_trips') || '[]');
      const updated = localTrips.map(t => t.id === tripId ? { ...t, status: newStatus } : t);
      localStorage.setItem('voyage_trips', JSON.stringify(updated));

      return { isSuccess: true };
    } catch (error) {
      return { isSuccess: false, error: error.message };
    }
  }
}

export default new TripService();
