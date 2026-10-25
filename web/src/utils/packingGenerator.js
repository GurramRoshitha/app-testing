import { parse } from 'date-fns';

const calculateDurationInDays = (datesString) => {
  if (!datesString) return 3;
  const parts = datesString.split('-');
  const startStr = (parts[0] || '').trim();
  const endStr = (parts[1] || startStr).trim();

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

  const startDate = parseTripDate(startStr);
  const endDate = parseTripDate(endStr) || startDate;

  if (startDate && endDate) {
    const diff = endDate.getTime() - startDate.getTime();
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24)) + 1;
    return Math.max(1, days);
  }
  return 3;
};

export const generatePackingSuggestions = (trip) => {
  if (!trip) return [];

  const items = [];
  const destLower = (trip.name || '').toLowerCase().trim();
  const dayCount = calculateDurationInDays(trip.dates);
  const members = trip.members || 1;

  // Features check
  const isBeach = ['goa', 'kovalam', 'gokarna', 'andaman', 'beach', 'puducherry', 'vizag', 'daman', 'lakshadweep', 'maldives', 'bali'].some(k => destLower.includes(k));
  const isCold = ['ooty', 'manali', 'shimla', 'dharamshala', 'spiti', 'kasol', 'munnar', 'darjeeling', 'gangtok', 'sikkim', 'mussoorie', 'nainital', 'ladakh', 'leh', 'kashmir', 'srinagar', 'gulmarg'].some(k => destLower.includes(k));
  const isReligious = ['tirupati', 'srisailam', 'varanasi', 'puri', 'shirdi', 'ayodhya', 'mathura', 'vrindavan', 'kedarnath', 'badrinath', 'haridwar', 'golden temple', 'amritsar', 'bodh gaya', 'rameswaram', 'madurai'].some(k => destLower.includes(k));
  const isTrekking = ['trek', 'hiking', 'safari', 'gir', "jim corbett", 'ranthambore', 'araku', 'valley of flowers'].some(k => destLower.includes(k));

  // --- CATEGORY 1: CLOTHING ---
  const shirtCount = Math.min(8, dayCount + 1);
  const pantsCount = Math.max(2, Math.floor(dayCount / 2) + 1);
  const undergarmentCount = Math.min(8, dayCount + 1);

  items.push({ id: `item_${Date.now()}_1`, name: "Shirts / Tops", category: "Clothing", quantity: `${shirtCount}`, isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_2`, name: "Pants / Shorts", category: "Clothing", quantity: `${pantsCount}`, isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_3`, name: "Undergarments", category: "Clothing", quantity: `${undergarmentCount}`, isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_4`, name: "Socks", category: "Clothing", quantity: `${shirtCount} pairs`, isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_5`, name: "Sleepwear / Nightwear", category: "Clothing", quantity: `${Math.max(1, Math.floor(dayCount / 2))} set(s)`, isPacked: false, isCustom: false });

  if (isCold) {
    items.push({ id: `item_${Date.now()}_6`, name: "Warm Jacket / Sweater", category: "Clothing", quantity: "1-2", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_7`, name: "Thermal Innerwear", category: "Clothing", quantity: "1-2 sets", isPacked: false, isCustom: false });
  }

  if (isBeach) {
    items.push({ id: `item_${Date.now()}_8`, name: "Swimwear / Beachwear", category: "Clothing", quantity: "2 sets", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_9`, name: "Breathable Linen Clothes", category: "Clothing", quantity: `${dayCount} sets`, isPacked: false, isCustom: false });
  }

  if (isReligious) {
    items.push({ id: `item_${Date.now()}_10`, name: "Modest / Traditional Attire", category: "Clothing", quantity: "2-3 sets", isPacked: false, isCustom: false });
  }

  // --- CATEGORY 2: TRAVEL ESSENTIALS ---
  items.push({ id: `item_${Date.now()}_11`, name: "Backpack / Daypack", category: "Travel Essentials", quantity: "1", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_12`, name: "Mobile Phone & Charger", category: "Travel Essentials", quantity: "1", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_13`, name: "Power Bank", category: "Travel Essentials", quantity: "1", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_14`, name: "Reusable Water Bottle", category: "Travel Essentials", quantity: "1", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_15`, name: "Sunglasses", category: "Travel Essentials", quantity: "1", isPacked: false, isCustom: false });

  // --- CATEGORY 3: TOILETRIES ---
  items.push({ id: `item_${Date.now()}_16`, name: "Toothbrush & Toothpaste", category: "Toiletries", quantity: "1 kit", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_17`, name: "Shampoo & Body Wash", category: "Toiletries", quantity: "1 travel kit", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_18`, name: "Face Wash & Moisturizer", category: "Toiletries", quantity: "1 set", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_19`, name: "Hand Sanitizer & Wipes", category: "Toiletries", quantity: "1 pack", isPacked: false, isCustom: false });

  // --- CATEGORY 4: WEATHER ESSENTIALS ---
  if (isBeach) {
    items.push({ id: `item_${Date.now()}_20`, name: "Sunscreen Lotion (SPF 50+)", category: "Weather Essentials", quantity: "1 bottle", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_21`, name: "Lip Balm with SPF", category: "Weather Essentials", quantity: "1", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_22`, name: "Wide-Brim Hat / Cap", category: "Weather Essentials", quantity: "1", isPacked: false, isCustom: false });
  } else if (isCold) {
    items.push({ id: `item_${Date.now()}_23`, name: "Cold Cream & Lip Balm", category: "Weather Essentials", quantity: "1 kit", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_24`, name: "Woolen Gloves & Cap", category: "Weather Essentials", quantity: "1 pair", isPacked: false, isCustom: false });
  } else {
    items.push({ id: `item_${Date.now()}_25`, name: "Sunscreen & Raincoat", category: "Weather Essentials", quantity: "1 set", isPacked: false, isCustom: false });
  }

  // --- CATEGORY 5: ACTIVITY-SPECIFIC ITEMS ---
  if (isTrekking) {
    items.push({ id: `item_${Date.now()}_26`, name: "Trekking Boots / Hiking Shoes", category: "Activity-Specific Items", quantity: "1 pair", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_27`, name: "Flashlight / Torch", category: "Activity-Specific Items", quantity: "1", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_28`, name: "Energy Bars & First Aid", category: "Activity-Specific Items", quantity: "1 pack", isPacked: false, isCustom: false });
  } else if (isBeach) {
    items.push({ id: `item_${Date.now()}_29`, name: "Flip-Flops / Beach Sandals", category: "Activity-Specific Items", quantity: "1 pair", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_30`, name: "Waterproof Phone Pouch", category: "Activity-Specific Items", quantity: "1", isPacked: false, isCustom: false });
  } else if (isReligious) {
    items.push({ id: `item_${Date.now()}_31`, name: "Easy Slip-On Sandals", category: "Activity-Specific Items", quantity: "1 pair", isPacked: false, isCustom: false });
    items.push({ id: `item_${Date.now()}_32`, name: "Temple Shoulder Shawl", category: "Activity-Specific Items", quantity: "1", isPacked: false, isCustom: false });
  } else {
    items.push({ id: `item_${Date.now()}_33`, name: "Comfortable Walking Shoes", category: "Activity-Specific Items", quantity: "1 pair", isPacked: false, isCustom: false });
  }

  // --- CATEGORY 6: DOCUMENTS AND SAFETY ---
  items.push({ id: `item_${Date.now()}_34`, name: "Government ID", category: "Documents and Safety", quantity: members > 1 ? `For ${members} travelers` : "1 copy", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_35`, name: "Tickets & Booking Vouchers", category: "Documents and Safety", quantity: "1 set", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_36`, name: "Emergency Cash & Cards", category: "Documents and Safety", quantity: "1 wallet", isPacked: false, isCustom: false });
  items.push({ id: `item_${Date.now()}_37`, name: "Personal Medicines & First Aid", category: "Documents and Safety", quantity: "1 pouch", isPacked: false, isCustom: false });

  return items;
};
