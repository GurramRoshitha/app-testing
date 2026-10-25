import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginScreen from './screens/LoginScreen';
import RegisterScreen from './screens/RegisterScreen';
import DashboardScreen from './screens/DashboardScreen';
import CreateTripScreen from './screens/CreateTripScreen';
import TripListScreen from './screens/TripListScreen';
import TripDetailsScreen from './screens/TripDetailsScreen';
import WeatherScreen from './screens/WeatherScreen';
import ProfileScreen from './screens/ProfileScreen';
import SettingsScreen from './screens/SettingsScreen';
import NotificationScreen from './screens/NotificationScreen';
import PackingChecklistScreen from './screens/PackingChecklistScreen';
import BudgetScreen from './screens/BudgetScreen';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginScreen />} />
        <Route path="/register" element={<RegisterScreen />} />
        <Route path="/dashboard" element={<DashboardScreen />} />
        <Route path="/create-trip" element={<CreateTripScreen />} />
        <Route path="/trip-list" element={<TripListScreen />} />
        <Route path="/trip-details/:id" element={<TripDetailsScreen />} />
        <Route path="/packing-checklist/:id" element={<PackingChecklistScreen />} />
        <Route path="/budget/:id" element={<BudgetScreen />} />
        <Route path="/budget" element={<BudgetScreen />} />
        <Route path="/weather/:destination" element={<WeatherScreen />} />
        <Route path="/notifications" element={<NotificationScreen />} />
        <Route path="/profile" element={<ProfileScreen />} />
        <Route path="/settings" element={<SettingsScreen />} />
        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
