export const analyzeItinerary = (itinerary) => {
  if (!itinerary) {
    return {
      hasTrekking: false,
      hasBeach: false,
      hasReligious: false,
      hasWildlife: false,
      hasCamping: false,
      hasSightseeing: true,
      summaryText: "Planned Activities: City Sightseeing"
    };
  }

  const allActivities = [];
  Object.keys(itinerary).forEach((day) => {
    const dayList = itinerary[day] || [];
    allActivities.push(...dayList);
  });

  let trekking = false;
  let beach = false;
  let religious = false;
  let wildlife = false;
  let camping = false;
  let sightseeing = false;

  allActivities.forEach((act) => {
    const text = `${act.title || ''} ${act.category || ''} ${act.description || ''}`.toLowerCase();

    if (text.includes("trek") || text.includes("hike") || text.includes("peak") || text.includes("climb") || text.includes("trail")) {
      trekking = true;
    }
    if (text.includes("beach") || text.includes("swim") || text.includes("cruise") || text.includes("island") || text.includes("scuba") || text.includes("snorkeling")) {
      beach = true;
    }
    if (text.includes("temple") || text.includes("darshan") || text.includes("pooja") || text.includes("shrine") || text.includes("church") || text.includes("mosque") || text.includes("gurudwara") || text.includes("ashram")) {
      religious = true;
    }
    if (text.includes("safari") || text.includes("jungle") || text.includes("wildlife") || text.includes("sanctuary") || text.includes("national park") || text.includes("zoo")) {
      wildlife = true;
    }
    if (text.includes("camp") || text.includes("tent") || text.includes("bonfire") || text.includes("stargazing")) {
      camping = true;
    }
    if (text.includes("sightseeing") || text.includes("fort") || text.includes("palace") || text.includes("museum") || text.includes("market") || text.includes("monument") || text.includes("viewpoint")) {
      sightseeing = true;
    }
  });

  const tags = [];
  if (trekking) tags.push("Trekking/Hiking");
  if (beach) tags.push("Beach Visit");
  if (religious) tags.push("Religious Visit");
  if (wildlife) tags.push("Wildlife Safari");
  if (camping) tags.push("Camping");
  if (sightseeing || tags.length === 0) tags.push("City Sightseeing");

  return {
    hasTrekking: trekking,
    hasBeach: beach,
    hasReligious: religious,
    hasWildlife: wildlife,
    hasCamping: camping,
    hasSightseeing: sightseeing,
    summaryText: `Planned Activities: ${tags.join(", ")}`
  };
};
