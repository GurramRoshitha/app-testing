const ExcelJS = require('exceljs');
const path = require('path');

async function createTestCasesExcel() {
  const workbook = new ExcelJS.Workbook();
  const sheet = workbook.addWorksheet('Final Test Cases');

  sheet.columns = [
    { header: 'Test ID', key: 'id', width: 12 },
    { header: 'Module / Feature', key: 'module', width: 25 },
    { header: 'Screen', key: 'screen', width: 22 },
    { header: 'Action / Test Input', key: 'action', width: 45 },
    { header: 'Expected Result', key: 'expected', width: 55 },
    { header: 'Status', key: 'status', width: 12 }
  ];

  const testCases = [
    { id: 'TC-01', module: 'Authentication', screen: 'Splash & Onboarding', action: 'Launch App & swipe onboarding slides', expected: 'Welcome Screen appears with "Get Started" & "Login" buttons', status: 'PASSED' },
    { id: 'TC-02', module: 'Authentication', screen: 'User Registration', action: 'Register with Name, Email & Password', expected: 'Account created in Firebase Auth & profile stored in Firestore', status: 'PASSED' },
    { id: 'TC-03', module: 'Authentication', screen: 'Email/Password Login', action: 'Enter registered Email & Password', expected: 'Authenticated successfully; redirects to Dashboard', status: 'PASSED' },
    { id: 'TC-04', module: 'Authentication', screen: 'Google Sign-In', action: 'Click "Sign in with Google"', expected: 'OAuth flow succeeds; user profile created in Firestore', status: 'PASSED' },
    { id: 'TC-05', module: 'Authentication', screen: 'Forgot Password', action: 'Enter email and click "Reset Password"', expected: 'Password reset email sent via Firebase Auth', status: 'PASSED' },
    { id: 'TC-06', module: 'Dashboard', screen: 'User Profile Header', action: 'View top bar on Dashboard', expected: 'Displays user name ("Good morning, Aarav 👋") & profile photo', status: 'PASSED' },
    { id: 'TC-07', module: 'Dashboard', screen: 'Quick Action Pills', action: 'Tap "My Trips", "Packing", "Budget", "Weather"', expected: 'Navigates directly to the corresponding screen', status: 'PASSED' },
    { id: 'TC-08', module: 'Dashboard', screen: 'Trip Banners', action: 'View Active, Upcoming & Recent sections', expected: 'Trips correctly grouped by dates; status badges display accurately', status: 'PASSED' },
    { id: 'TC-09', module: 'Dashboard', screen: 'Reminder Popup', action: 'View popup for trip starting in ≤ 3 days', expected: 'Popup banner displays days remaining; dismiss removes popup', status: 'PASSED' },
    { id: 'TC-10', module: 'Trip Planning', screen: 'Plan New Journey', action: 'Enter Destination, Start & End Dates', expected: 'AI analyzes location & generates multi-day itinerary slots', status: 'PASSED' },
    { id: 'TC-11', module: 'Trip Planning', screen: 'Cloud & Local Sync', action: 'Save generated trip', expected: 'Trip saved to Cloud Firestore & cached in local disk storage', status: 'PASSED' },
    { id: 'TC-12', module: 'Trip Planning', screen: 'Multi-Device Sync', action: 'Create trip on Web or Android', expected: 'Trip appears automatically on both Web and Android apps', status: 'PASSED' },
    { id: 'TC-13', module: 'Trip Details', screen: 'Itinerary View', action: 'View daily activities (Morning, Sightseeing, Lunch)', expected: 'Activities, times, categories, and descriptions render clearly', status: 'PASSED' },
    { id: 'TC-14', module: 'Trip Details', screen: 'Export PDF', action: 'Click "Export PDF" button', expected: 'Generates and prints formatted PDF itinerary document', status: 'PASSED' },
    { id: 'TC-15', module: 'Trip Details', screen: 'Google Maps Link', action: 'Click "Open in Google Maps"', expected: 'Opens location coordinates directly inside Google Maps app', status: 'PASSED' },
    { id: 'TC-16', module: 'Trip Details', screen: 'Update Trip Status', action: 'Change status from "Upcoming" to "Completed"', expected: 'Status updates in real-time across Web and Android', status: 'PASSED' },
    { id: 'TC-17', module: 'Trip Details', screen: 'Delete Trip', action: 'Click "Delete Trip"', expected: 'Document removed from Firestore & UI updates bi-directionally', status: 'PASSED' },
    { id: 'TC-18', module: 'Packing Checklist', screen: 'Smart Checklist', action: 'Open packing screen for destination', expected: 'Auto-generates categorised items (Clothing, Toiletries, Weather)', status: 'PASSED' },
    { id: 'TC-19', module: 'Packing Checklist', screen: 'Item Toggle', action: 'Tap checkbox on item', expected: 'Item marks as packed/unpacked; progress bar updates', status: 'PASSED' },
    { id: 'TC-20', module: 'Packing Checklist', screen: 'Add Custom Item', action: 'Click "+ Add Item", enter name & quantity', expected: 'Custom item added to category list and saved to storage', status: 'PASSED' },
    { id: 'TC-21', module: 'Budget Tracker', screen: 'Group Expense Tracker', action: 'Add new expense (Title, Amount, Paid By)', expected: 'Budget progress updates; expense added to breakdown list', status: 'PASSED' },
    { id: 'TC-22', module: 'Live Weather', screen: 'Weather Forecast', action: 'Search destination weather', expected: 'Nominatim geocodes location & Open-Meteo returns 7-day forecast', status: 'PASSED' },
    { id: 'TC-23', module: 'Notifications', screen: 'Departure & Status Alerts', action: 'Sync trip dates with notifications', expected: 'Generates 3-day countdown & completion notifications', status: 'PASSED' },
    { id: 'TC-24', module: 'Notifications', screen: 'Notification Actions', action: 'Mark as Read / Delete / Clear All', expected: 'UI updates & recorded event IDs prevent deleted alerts from reappearing', status: 'PASSED' },
    { id: 'TC-25', module: 'Profile & Settings', screen: 'Profile Edit & Logout', action: 'Update name/photo & click Logout', expected: 'Profile updates in Firestore; user logged out to Welcome screen', status: 'PASSED' }
  ];

  // Header Style
  const headerRow = sheet.getRow(1);
  headerRow.height = 28;
  headerRow.font = { name: 'Calibri', size: 11, bold: true, color: { argb: 'FFFFFF' } };
  headerRow.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: '1E293B' } };
  headerRow.alignment = { vertical: 'middle', horizontal: 'center' };

  testCases.forEach((tc) => {
    const row = sheet.addRow(tc);
    row.height = 22;
    row.font = { name: 'Calibri', size: 10 };
    row.alignment = { vertical: 'middle' };

    // Format Status Column (Green pill for PASSED)
    const statusCell = row.getCell('status');
    statusCell.alignment = { vertical: 'middle', horizontal: 'center' };
    statusCell.font = { name: 'Calibri', size: 10, bold: true, color: { argb: '166534' } };
    statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'DCFCE7' } };
  });

  const filePath = path.resolve(__dirname, 'final_test_cases.xlsx');
  await workbook.xlsx.writeFile(filePath);
  console.log("Successfully created " + filePath);
}

createTestCasesExcel();
