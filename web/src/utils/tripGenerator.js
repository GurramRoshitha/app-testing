import { stateHubs, districtPlaces } from './knowledgeBase';
import { differenceInDays, parse } from 'date-fns';

export const generateItinerary = (destination, startDate, endDate, budget, currency) => {
  const parseDate = (dateStr) => {
    let d = parse(dateStr, 'MMM dd, yyyy', new Date());
    if (isNaN(d.getTime())) {
      d = parse(dateStr, 'yyyy-MM-dd', new Date());
    }
    return isNaN(d.getTime()) ? new Date() : d;
  };

  const start = parseDate(startDate);
  const end = parseDate(endDate);
  const dayCount = Math.max(1, differenceInDays(end, start) + 1);

  const destinationLower = (destination || '').toLowerCase().trim();
  const hubs = stateHubs[destinationLower] || [destination || 'Destination'];

  const currencySymbols = { "USD": "$", "EUR": "€", "GBP": "£", "INR": "₹" };
  const symbol = currencySymbols[currency] || "$";

  const generatedItinerary = {};
  const validBudget = parseFloat(budget) || 1000;
  const dailyBudget = validBudget / dayCount;
  let totalAllocated = 0;

  // Fixed: Uses .length instead of .size for JavaScript Arrays
  const daysPerHub = Math.max(1, dayCount / hubs.length);

  for (let day = 1; day <= dayCount; day++) {
    const dayActivities = [];
    const currentHubIndex = Math.min(hubs.length - 1, Math.floor((day - 1) / daysPerHub));
    const currentHub = hubs[currentHubIndex] || destination || 'Destination';

    const isFirstDayInHub = (day === 1) || (Math.floor((day - 1) % daysPerHub) === 0 && currentHubIndex > 0);

    // 1. Morning Slot
    const morningSlotTime = "08:00 AM";
    if (isFirstDayInHub) {
      const title = day === 1 ? `Check-in at Premium Hotel in ${currentHub}` : `Checkout & Travel to ${currentHub}`;
      const desc = day === 1 ? `Starting your journey in ${currentHub}. Settle into your centrally located base.`
                               : `Moving to the next district. Enjoy a scenic morning transit to ${currentHub}.`;

      dayActivities.push({ time: morningSlotTime, title, category: "Accommodation", cost: "", description: desc, type: "Stay" });
    } else {
      dayActivities.push({ time: morningSlotTime, title: `Leisurely Morning in ${currentHub}`, category: "Leisure", cost: "", description: "Wake up and enjoy the local morning vibes.", type: "Activity" });
    }

    const hubKey = currentHub.toLowerCase().trim();
    const hubPlaces = districtPlaces[hubKey] || ["Local Attraction", "City Landmark", "Scenic Spot", "Cultural Site", "Historic Monument"];

    // 2. Sightseeing Slot
    const sightAct = hubPlaces[day % hubPlaces.length];
    dayActivities.push({ time: "10:30 AM", title: sightAct, category: "Sightseeing", cost: "", description: `Visit ${sightAct}, a must-see spot in ${currentHub}.`, type: "Activity" });

    // 3. Lunch Slot
    dayActivities.push({ time: "01:30 PM", title: `Authentic ${currentHub} Lunch`, category: "Restaurant", cost: "", description: `Taste local flavors at a top-rated ${currentHub} eatery.`, type: "Restaurant" });

    // 4. Cultural Slot
    const cultAct = hubPlaces[(day + 1) % hubPlaces.length];
    dayActivities.push({ time: "04:30 PM", title: cultAct, category: "Culture", cost: "", description: `Experience the unique heritage of ${cultAct}.`, type: "Activity" });

    // 5. Evening Slot
    dayActivities.push({ time: "07:30 PM", title: `${currentHub} Evening Exploration`, category: "Leisure", cost: "", description: `Wind down the day with a relaxing evening in ${currentHub}.`, type: "Activity" });

    generatedItinerary[day.toString()] = dayActivities;
  }

  return generatedItinerary;
};

export const exportTripToPdf = (trip) => {
  if (!trip) return;

  const days = Object.keys(trip.itinerary || {}).sort((a, b) => parseInt(a) - parseInt(b));

  const printWindow = window.open('', '_blank');
  if (!printWindow) {
    alert("Please allow popups to export the PDF itinerary.");
    return;
  }

  const currencySymbols = { USD: "$", EUR: "€", GBP: "£", INR: "₹" };
  const symbol = currencySymbols[trip.currency] || trip.currency || "$";

  let daysHtml = '';
  days.forEach(dayKey => {
    const activities = trip.itinerary[dayKey] || [];
    daysHtml += `
      <div class="day-section">
        <h3 class="day-title">Day ${dayKey} Itinerary</h3>
        <div class="activities-list">
    `;

    activities.forEach(act => {
      daysHtml += `
        <div class="activity-card">
          <div class="activity-header">
            <span class="activity-time">${act.time || ''}</span>
            ${act.cost ? `<span class="activity-cost">${act.cost}</span>` : ''}
          </div>
          <h4 class="activity-title">${act.title}</h4>
          <div class="activity-badge">${act.type || 'Activity'} &bull; ${act.category || ''}</div>
          <p class="activity-desc">${act.description || ''}</p>
        </div>
      `;
    });

    daysHtml += `
        </div>
      </div>
    `;
  });

  const htmlContent = `
    <!DOCTYPE html>
    <html>
      <head>
        <title>Voyage Itinerary - ${trip.name}</title>
        <style>
          @page { size: A4; margin: 20mm; }
          body { font-family: 'Helvetica Neue', Arial, sans-serif; color: #111; padding: 20px; line-height: 1.5; }
          .header { border-bottom: 2px solid #6200EE; padding-bottom: 12px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: flex-end; }
          .app-title { color: #6200EE; font-size: 24px; font-weight: 800; margin: 0; }
          .sub-title { color: #666; font-size: 14px; margin-top: 4px; }
          .summary-card { background: #f8f9fa; border-radius: 12px; padding: 16px; margin-bottom: 24px; border: 1px solid #e9ecef; }
          .summary-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 14px; }
          .summary-item label { font-weight: bold; color: #495057; display: block; font-size: 11px; text-transform: uppercase; letter-spacing: 0.5px; }
          .day-section { margin-bottom: 24px; page-break-inside: avoid; }
          .day-title { font-size: 18px; font-weight: 700; color: #6200EE; border-bottom: 1px solid #dee2e6; padding-bottom: 6px; margin-bottom: 12px; }
          .activity-card { background: #fff; border: 1px solid #e9ecef; border-radius: 8px; padding: 12px; margin-bottom: 10px; page-break-inside: avoid; }
          .activity-header { display: flex; justify-content: space-between; font-weight: bold; font-size: 13px; margin-bottom: 4px; }
          .activity-time { color: #6200EE; }
          .activity-cost { color: #2e7d32; }
          .activity-title { font-size: 16px; font-weight: 800; margin: 4px 0; }
          .activity-badge { font-size: 10px; font-weight: 700; text-transform: uppercase; color: #666; margin-bottom: 6px; }
          .activity-desc { font-size: 13px; color: #495057; margin: 0; }
          .footer { text-align: center; margin-top: 30px; font-size: 11px; color: #888; border-top: 1px solid #eee; padding-top: 10px; }
        </style>
      </head>
      <body>
        <div class="header">
          <div>
            <h1 class="app-title">AI-Based Travel Planner</h1>
            <div class="sub-title">Voyage AI &bull; Smart Travel Itinerary</div>
          </div>
          <div style="text-align: right; font-size: 12px; color: #666;">
            Generated on ${new Date().toLocaleDateString()}
          </div>
        </div>

        <div class="summary-card">
          <div class="summary-grid">
            <div class="summary-item">
              <label>Destination</label>
              <strong>${trip.name}</strong>
            </div>
            <div class="summary-item">
              <label>Dates</label>
              <strong>${trip.dates}</strong>
            </div>
            <div class="summary-item">
              <label>Travelers</label>
              <strong>${trip.members} Person(s)</strong>
            </div>
            <div class="summary-item">
              <label>Total Budget</label>
              <strong>${symbol} ${trip.total_budget ? trip.total_budget.toLocaleString() : '0'}</strong>
            </div>
            ${trip.travel_pace ? `<div class="summary-item"><label>Travel Pace</label><strong>${trip.travel_pace}</strong></div>` : ''}
            ${trip.interests ? `<div class="summary-item"><label>Interests</label><strong>${trip.interests}</strong></div>` : ''}
          </div>
        </div>

        ${daysHtml}

        <div class="footer">
          Generated by Voyage AI Travel Planner &bull; Have a safe and wonderful journey!
        </div>

        <script>
          window.onload = function() {
            setTimeout(function() {
              window.print();
            }, 500);
          }
        </script>
      </body>
    </html>
  `;

  printWindow.document.write(htmlContent);
  printWindow.document.close();
};



export const getEffectiveStatus = (trip) => {
  if (!trip || !trip.dates) {
    return (trip && trip.status === 'Completed') ? 'Completed' : 'Upcoming';
  }

  const parts = trip.dates.split('-');
  const startStr = (parts[0] || '').trim();
  const endStr = (parts[1] || startStr).trim();

  const parseDateStr = (str) => {
    if (!str) return null;
    let d = parse(str, 'MMM dd, yyyy', new Date());
    if (isNaN(d.getTime())) {
      d = parse(str, 'yyyy-MM-dd', new Date());
    }
    if (isNaN(d.getTime())) {
      d = new Date(str);
    }
    return isNaN(d.getTime()) ? null : d;
  };

  const startDate = parseDateStr(startStr);
  const endDate = parseDateStr(endStr) || startDate;

  if (!startDate) {
    return (trip && trip.status === 'Completed') ? 'Completed' : 'Upcoming';
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const startCal = new Date(startDate);
  startCal.setHours(0, 0, 0, 0);

  const endCal = new Date(endDate);
  endCal.setHours(23, 59, 59, 999);

  if (today.getTime() > endCal.getTime()) {
    return 'Completed';
  } else if (today.getTime() >= startCal.getTime()) {
    return 'Active';
  } else if (trip.status === 'Completed') {
    return 'Completed';
  } else {
    return 'Upcoming';
  }
};