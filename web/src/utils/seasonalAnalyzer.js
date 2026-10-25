import { parse } from 'date-fns';
import { TERRAIN_TYPES } from './destinationAnalyzer';

export const SEASON_TYPES = {
  WINTER: "Winter Season (Cold / Pleasant)",
  SUMMER: "Summer Season (Warm / Hot)",
  MONSOON: "Monsoon Season (Rainy / Humid)",
  MODERATE: "Moderate Climate"
};

export const analyzeSeason = (datesString, terrainType) => {
  const month = parseMonthFromDates(datesString);

  let season = SEASON_TYPES.MODERATE;
  if ([11, 12, 1, 2].includes(month)) season = SEASON_TYPES.WINTER;
  else if ([3, 4, 5, 6].includes(month)) season = SEASON_TYPES.SUMMER;
  else if ([7, 8, 9, 10].includes(month)) season = SEASON_TYPES.MONSOON;

  let desc = "Favorable Travel Climate";
  if (season === SEASON_TYPES.WINTER && terrainType === TERRAIN_TYPES.MOUNTAIN) {
    desc = "Heavy Cold / Possible Frost & Chilly Winds";
  } else if (season === SEASON_TYPES.WINTER) {
    desc = "Pleasant Days with Mild to Cool Nights";
  } else if (season === SEASON_TYPES.SUMMER && terrainType === TERRAIN_TYPES.MOUNTAIN) {
    desc = "Pleasant Hill Climate with Cool Breeze";
  } else if (season === SEASON_TYPES.SUMMER && terrainType === TERRAIN_TYPES.DESERT) {
    desc = "Extreme Heat during Days; Cool Nights";
  } else if (season === SEASON_TYPES.SUMMER) {
    desc = "Hot & Dry / High Sun Exposure";
  } else if (season === SEASON_TYPES.MONSOON && (terrainType === TERRAIN_TYPES.COASTAL || terrainType === TERRAIN_TYPES.MOUNTAIN)) {
    desc = "Frequent Rain Showers & High Humidity";
  } else if (season === SEASON_TYPES.MONSOON) {
    desc = "Moderate to Heavy Rainfall";
  }

  return {
    seasonType: season,
    description: desc
  };
};

const parseMonthFromDates = (datesString) => {
  if (!datesString) return new Date().getMonth() + 1;
  const startDateStr = datesString.split('-')[0]?.trim() || datesString.trim();

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

  const parsed = parseTripDate(startDateStr);
  return parsed ? parsed.getMonth() + 1 : new Date().getMonth() + 1;
};
