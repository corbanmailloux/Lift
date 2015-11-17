// Check for localStorage
function storageAvailable(type) {
  try {
    var storage = window[type],
      x = '__storage_test__';
    storage.setItem(x, x);
    storage.removeItem(x);
    return true;
  }
  catch(e) {
    return false;
  }
}

// Test first
if (!storageAvailable("localStorage")) {
  document.getElementById("noLocalStorage").style.display = "block";
  document.getElementById("mainContent").style.display = "none";
}

var lastLift = 0;
var currentStreak = 1;
var bestStreak = 1;
var now = moment();

// Fragile: If the user removes either of the streak values, bad things happen.
if (localStorage.lastLift) { 
  lastLift = moment.unix(localStorage.lastLift);
  currentStreak = localStorage.currentStreak;
  bestStreak = localStorage.bestStreak;
}

if (lastLift) {
  var daysBetween = (lastLift.startOf("day")).diff((now.startOf("day")), "days");

  switch (daysBetween) {
    case 0:
      // Already lifted today
      document.getElementById("headerText").innerHTML = "You already lifted today!";
      break;
    case 1:
      currentStreak++; // Intentional fallthrough
    default:
      if (daysBetween > 1) {
        // Too long; reset the streak
        currentStreak = 1;
      }

      document.getElementById("headerText").innerHTML = "Great lift. Same time tomorrow?";
      break;
  }
} else {
  document.getElementById("headerText").innerHTML = "Great first day. Same time tomorrow?";
}

// Update bestStreak
bestStreak = Math.max(bestStreak, currentStreak);

// Update lastLift
lastLift = now;

document.getElementById("currentStreak").innerHTML = currentStreak;
document.getElementById("bestStreak").innerHTML = bestStreak;

// Store the updated values
localStorage.setItem("lastLift", lastLift.unix());
localStorage.setItem("currentStreak", currentStreak);
localStorage.setItem("bestStreak", bestStreak);
