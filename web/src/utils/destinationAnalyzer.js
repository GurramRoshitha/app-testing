export const TERRAIN_TYPES = {
  COASTAL: "Coastal & Beach Environment",
  MOUNTAIN: "Mountainous & High Altitude",
  DESERT: "Desert & Arid Region",
  FOREST: "Forest & Wildlife Sanctuary",
  PILGRIMAGE: "Cultural & Pilgrimage Destination",
  URBAN: "Urban & City Environment"
};

export const analyzeDestination = (destinationName) => {
  const dest = (destinationName || '').toLowerCase().trim();

  const isCoastal = [
    "goa", "kovalam", "gokarna", "andaman", "nicobar", "beach", "pondicherry", "puducherry",
    "vizag", "visakhapatnam", "alibaug", "digha", "daman", "diu", "lakshadweep", "maldives",
    "bali", "phuket", "hawaii", "varkala", "puri", "kanyakumari", "mahabalipuram", "chennai",
    "mumbai", "udupi", "mangalore", "dhanushkodi", "kashid", "tarkarli", "ganpatipule"
  ].some(k => dest.includes(k));

  const isMountainous = [
    "ooty", "manali", "shimla", "dharamshala", "dalhousie", "spiti", "kasol", "munnar",
    "kodaikanal", "coorg", "darjeeling", "gangtok", "sikkim", "tawang", "shillong", "mussoorie",
    "nainital", "rishikesh", "ladakh", "leh", "kashmir", "srinagar", "gulmarg", "pahalgam",
    "chamba", "lonavala", "mahabaleshwar", "horsley hills", "araku", "chikmagalur", "wayanad",
    "mount abu", "pelling", "lachung", "hills", "valley", "peak", "heights", "alps"
  ].some(k => dest.includes(k));

  const isDesert = [
    "jaisalmer", "jodhpur", "bikaner", "kutch", "thar", "rann of kutch", "desert", "pushkar",
    "barmer", "dunes"
  ].some(k => dest.includes(k));

  const isForest = [
    "jim corbett", "kaziranga", "gir", "ranthambore", "sundarbans", "bandhavgarh", "kanha",
    "periyar", "thekkady", "wayanad", "mudumalai", "bandipur", "kabini", "nagarhole",
    "dandeli", "forest", "sanctuary", "national park", "wildlife", "safari"
  ].some(k => dest.includes(k));

  const isPilgrimage = [
    "tirupati", "srisailam", "varanasi", "puri", "shirdi", "ayodhya", "mathura", "vrindavan",
    "kedarnath", "badrinath", "haridwar", "golden temple", "amritsar", "bodh gaya", "deoghar",
    "somnath", "dwarka", "rameswaram", "madurai", "kanyakumari", "ujjain", "trimbakeshwar",
    "pawapuri", "nalanda", "rajgir", "katra", "vaishno devi", "palani", "sabari", "temple", "shrine"
  ].some(k => dest.includes(k));

  let terrain = TERRAIN_TYPES.URBAN;
  if (isCoastal) terrain = TERRAIN_TYPES.COASTAL;
  else if (isMountainous) terrain = TERRAIN_TYPES.MOUNTAIN;
  else if (isDesert) terrain = TERRAIN_TYPES.DESERT;
  else if (isForest) terrain = TERRAIN_TYPES.FOREST;
  else if (isPilgrimage) terrain = TERRAIN_TYPES.PILGRIMAGE;

  return {
    destinationName,
    terrainType: terrain,
    summaryText: terrain
  };
};
