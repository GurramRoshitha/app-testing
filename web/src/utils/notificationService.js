import { parse } from 'date-fns';
import AuthService from '../data/services/AuthService';
import { getEffectiveStatus } from './tripGenerator';

const getStorageKey = () => {
  const user = AuthService.getCurrentUser();
  const userId = user?.uid || 'guest_user';
  return `voyage_notifications_${userId}`;
};

const getRecordedEventsKey = () => {
  const user = AuthService.getCurrentUser();
  const userId = user?.uid || 'guest_user';
  return `voyage_recorded_events_${userId}`;
};

const getShownPopupsKey = () => {
  const user = AuthService.getCurrentUser();
  const userId = user?.uid || 'guest_user';
  return `voyage_shown_popups_${userId}`;
};

export const getNotifications = () => {
  try {
    const key = getStorageKey();
    return JSON.parse(localStorage.getItem(key) || '[]');
  } catch (e) {
    return [];
  }
};

export const saveNotifications = (notifications) => {
  try {
    const key = getStorageKey();
    localStorage.setItem(key, JSON.stringify(notifications));
  } catch (e) {
    console.error("Failed saving notifications:", e);
  }
};

const getRecordedEvents = () => {
  try {
    const key = getRecordedEventsKey();
    return new Set(JSON.parse(localStorage.getItem(key) || '[]'));
  } catch (e) {
    return new Set();
  }
};

const saveRecordedEvents = (eventsSet) => {
  try {
    const key = getRecordedEventsKey();
    localStorage.setItem(key, JSON.stringify(Array.from(eventsSet)));
  } catch (e) {
    console.error("Failed saving recorded events:", e);
  }
};

const getShownPopups = () => {
  try {
    const key = getShownPopupsKey();
    return new Set(JSON.parse(localStorage.getItem(key) || '[]'));
  } catch (e) {
    return new Set();
  }
};

const saveShownPopups = (popupsSet) => {
  try {
    const key = getShownPopupsKey();
    localStorage.setItem(key, JSON.stringify(Array.from(popupsSet)));
  } catch (e) {
    console.error("Failed saving shown popups:", e);
  }
};

export const getUnreadCount = () => {
  const list = getNotifications();
  return list.filter(n => !n.isRead).length;
};

export const markAsRead = (id) => {
  const list = getNotifications();
  const updated = list.map(n => n.id === id ? { ...n, isRead: true } : n);
  saveNotifications(updated);
  return updated;
};

export const markAllAsRead = () => {
  const list = getNotifications();
  const updated = list.map(n => ({ ...n, isRead: true }));
  saveNotifications(updated);
  return updated;
};

export const deleteNotification = (id) => {
  const list = getNotifications();
  const updated = list.filter(n => n.id !== id);
  saveNotifications(updated);
  return updated;
};

export const clearAllNotifications = () => {
  saveNotifications([]);
  return [];
};

const parseTripDate = (dateStr) => {
  if (!dateStr) return null;
  let d = parse(dateStr, 'MMM dd, yyyy', new Date());
  if (isNaN(d.getTime())) {
    d = parse(dateStr, 'yyyy-MM-dd', new Date());
  }
  if (isNaN(d.getTime())) {
    d = new Date(dateStr);
  }
  return isNaN(d.getTime()) ? null : d;
};

export const syncNotificationsFromTrips = (trips) => {
  if (!trips || !Array.isArray(trips)) return getNotifications();

  const currentNotifications = getNotifications();
  const recordedEvents = getRecordedEvents();

  const user = AuthService.getCurrentUser();
  const userId = user?.uid || 'guest_user';

  // Filter trips belonging to current user
  const userTrips = trips.filter(t => !t.user_id || t.user_id === userId);

  const newNotifications = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  userTrips.forEach((trip) => {
    if (!trip || !trip.id || !trip.dates) return;

    const parts = trip.dates.split('-');
    const startStr = (parts[0] || '').trim();
    const endStr = (parts[1] || startStr).trim();

    const startDate = parseTripDate(startStr);
    const endDate = parseTripDate(endStr) || startDate;

    // 1. Trip Start Event
    if (startDate) {
      const startCal = new Date(startDate);
      startCal.setHours(0, 0, 0, 0);

      const diffTime = startCal.getTime() - today.getTime();
      const daysRemaining = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (daysRemaining >= 0 && daysRemaining <= 3) {
        const startEventId = `${trip.id}_start_${daysRemaining}`;
        if (!recordedEvents.has(startEventId)) {
          recordedEvents.add(startEventId);
          let title = "Your Trip Starts Soon!";
          let message = `Your trip to ${trip.name} starts in ${daysRemaining} days!`;

          if (daysRemaining === 0) {
            title = "Your Trip Starts Today!";
            message = `Your trip to ${trip.name} starts today! Have a wonderful journey!`;
          } else if (daysRemaining === 1) {
            title = "Your Trip Starts Tomorrow!";
            message = `Your trip to ${trip.name} starts tomorrow! Get ready for your adventure!`;
          } else if (daysRemaining === 2) {
            title = "Your Trip Starts in 2 Days!";
            message = `Your trip to ${trip.name} starts in 2 days!`;
          } else if (daysRemaining === 3) {
            title = "Your Trip Starts in 3 Days!";
            message = `Your trip to ${trip.name} starts in 3 days!`;
          }

          newNotifications.push({
            id: startEventId,
            tripId: trip.id,
            userId: userId,
            title,
            message,
            type: "TRIP_START",
            timestamp: Date.now(),
            isRead: false
          });
        }
      }
    }

    // 2. Trip End / Completion Event
    if (endDate) {
      const endCal = new Date(endDate);
      endCal.setHours(23, 59, 59, 999);

      if (today.getTime() > endCal.getTime()) {
        const endEventId = `${trip.id}_completed`;
        if (!recordedEvents.has(endEventId)) {
          recordedEvents.add(endEventId);
          newNotifications.push({
            id: endEventId,
            tripId: trip.id,
            userId: userId,
            title: "Your Trip is Completed!",
            message: `Your trip to ${trip.name} is completed! We hope you had a wonderful adventure!`,
            type: "TRIP_END",
            timestamp: Date.now(),
            isRead: false
          });
        }
      }
    }
  });

  saveRecordedEvents(recordedEvents);

  if (newNotifications.length > 0) {
    const updated = [...newNotifications, ...currentNotifications].sort((a, b) => b.timestamp - a.timestamp);
    saveNotifications(updated);
    return updated;
  }

  return currentNotifications;
};

export const checkAndGetActivePopups = (trips) => {
  if (!trips || !Array.isArray(trips)) return [];

  const shownPopups = getShownPopups();
  const user = AuthService.getCurrentUser();
  const userId = user?.uid || 'guest_user';
  const userTrips = trips.filter(t => !t.user_id || t.user_id === userId);

  const activePopups = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  userTrips.forEach((trip) => {
    if (!trip || !trip.id || !trip.dates) return;

    const parts = trip.dates.split('-');
    const startStr = (parts[0] || '').trim();
    const endStr = (parts[1] || startStr).trim();

    const startDate = parseTripDate(startStr);
    const endDate = parseTripDate(endStr) || startDate;

    if (startDate) {
      const startCal = new Date(startDate);
      startCal.setHours(0, 0, 0, 0);

      const diffTime = startCal.getTime() - today.getTime();
      const daysRemaining = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (daysRemaining >= 0 && daysRemaining <= 3) {
        const popupId = `popup_${trip.id}_start_${daysRemaining}`;
        if (!shownPopups.has(popupId)) {
          let title = "Your Trip Starts Soon!";
          let message = `Your trip to ${trip.name} starts in ${daysRemaining} days!`;

          if (daysRemaining === 0) {
            title = "Your Trip Starts Today!";
            message = `Your trip to ${trip.name} starts today! Have a wonderful journey!`;
          } else if (daysRemaining === 1) {
            title = "Your Trip Starts Tomorrow!";
            message = `Your trip to ${trip.name} starts tomorrow! Get ready for your adventure!`;
          } else if (daysRemaining === 2) {
            title = "Your Trip Starts in 2 Days!";
            message = `Your trip to ${trip.name} starts in 2 days!`;
          } else if (daysRemaining === 3) {
            title = "Your Trip Starts in 3 Days!";
            message = `Your trip to ${trip.name} starts in 3 days!`;
          }

          activePopups.push({
            popupId,
            tripId: trip.id,
            tripName: trip.name,
            title,
            message,
            type: "TRIP_START",
            daysRemaining
          });
        }
      }
    }

    if (endDate) {
      const endCal = new Date(endDate);
      endCal.setHours(23, 59, 59, 999);

      if (today.getTime() > endCal.getTime()) {
        const popupId = `popup_${trip.id}_completed`;
        if (!shownPopups.has(popupId)) {
          activePopups.push({
            popupId,
            tripId: trip.id,
            tripName: trip.name,
            title: "Your Trip is Completed!",
            message: `Your trip to ${trip.name} is completed! We hope you had a wonderful adventure!`,
            type: "TRIP_COMPLETED",
            daysRemaining: -1
          });
        }
      }
    }
  });

  return activePopups;
};

export const markPopupAsDismissed = (popupId) => {
  const shownPopups = getShownPopups();
  shownPopups.add(popupId);
  saveShownPopups(shownPopups);
};
