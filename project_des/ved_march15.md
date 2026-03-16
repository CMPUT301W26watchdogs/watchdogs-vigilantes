# mar 15 - Fix Re-opened User Stories + UI Enhancement

## overview

four re-opened user stories fixed on branch `UI_Revamp+Code_Documentation`:
1. US 02.06.04 — organizer cancel entrants
2. US 02.02.02 — organizer map view of entrant locations
3. US 01.05.05 — entrant lottery criteria/guidelines
4. US 01.05.04 — entrant waitlist count

also enhanced the full UI to match `Project301_facelift.pdf` mockup.

---

## US 02.06.04 — cancel entrants that did not sign up

### what was missing
- `viewAttendee.java` was read-only, no way for organizer to cancel entrants

### what was done
- rewrote `viewAttendee.java` to use `EntrantAdapter` instead of `ProfileAdapter`
- `EntrantAdapter` now accepts an optional `eventId` — when provided, shows a "Cancel" button per entrant
- cancel button sets the attendee's Firestore status to `"cancelled"`
- added "Cancel All Pending" button to `attendee_list.xml` header for bulk cancellation
- added "Map" button in waiting list header to navigate to `EntrantMapActivity`

### files changed
- `viewAttendee.java` — full rewrite with cancel functionality
- `EntrantAdapter.java` — added second constructor with eventId, cancel button logic
- `item_entrant.xml` — added `cancelEntrantButton` MaterialButton
- `attendee_list.xml` — redesigned with header bar, map button, cancel all button

---

## US 02.02.02 — map where entrants joined waiting list from

### what was missing
- `EntrantMapActivity` existed but queried wrong collection (`waitingList` instead of `attendees`)
- geolocation was never captured during sign-up

### what was done
- `EntrantMapActivity.java` — fixed to query `attendees` collection (matches rest of codebase)
- removed placeholder/fake markers — now shows toast if no location data available
- `EventDetailActivity.java` — `performSignUp()` now captures device GPS coordinates using `FusedLocationProviderClient`
- if event has `geolocationRequired: true`, location permission is requested before sign-up
- latitude/longitude stored in attendee document in Firestore
- added `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions to `AndroidManifest.xml`
- added `play-services-location:21.1.0` dependency to `build.gradle.kts`

### files changed
- `EntrantMapActivity.java` — query fix, removed placeholders
- `EventDetailActivity.java` — geolocation capture on sign-up
- `AndroidManifest.xml` — location permissions + missing activity declarations
- `app/build.gradle.kts` — play-services-location dependency

---

## US 01.05.05 — lottery selection criteria/guidelines

### what was missing
- `LotteryInfoActivity` showed event name, draw date, total spots — but no actual criteria or guidelines explaining how the lottery works

### what was done
- `LotteryInfoActivity.java` — added lottery criteria text dynamically:
  1. equal chance for all entrants on waiting list
  2. random selection of N entrants after registration closes
  3. decline → replacement drawn from remaining waitlist
  4. no priority based on sign-up time
  5. can cancel entry before draw
- also now shows total waitlist count from Firestore
- also checks `attendees` collection instead of `waitingList` for user status
- `activity_lottery_info.xml` — redesigned with stats row cards (Total Spots, On Waitlist, Draw Date), status section, and SELECTION CRITERIA section

### files changed
- `LotteryInfoActivity.java` — criteria text, waitlist count, fixed collection query
- `activity_lottery_info.xml` — full redesign with criteria section

---

## US 01.05.04 — total entrants on waiting list

### what was missing
- `EventDetailActivity` had a `waitlistCount` TextView in layout but never populated it
- event cards in `EventAdapter` had `waitingCount` but never queried Firestore for it

### what was done
- `EventDetailActivity.java` — added `loadWaitlistCount()` method that queries `attendees` collection where `status == "pending"` and sets count
- `EventAdapter.java` — added Firestore query in `onBindViewHolder` to count pending attendees and display on each event card as "X Waiting"
- also shows location + date info on event cards

### files changed
- `EventDetailActivity.java` — waitlist count query
- `EventAdapter.java` — per-card waitlist count + location info

---

## UI enhancement (matching Project301_facelift.pdf)

### bottom navigation
- added `BottomNavigationView` with 4 tabs (Events, Home, Alerts, Profile) across all main screens
- created `res/menu/bottom_nav_menu.xml`
- wired up navigation in `HomePage`, `AllEventsActivity`, `ProfilePage`, `EventDetailActivity`, `AddEvent`

### screens updated

| Screen | Changes |
|---|---|
| Home | settings icon, icons in quick access cards, "My Alerts" card, bottom nav |
| Events List | VIGILANTE header, subtitle, category filter chips (All/Sports/Arts/Music), bottom nav |
| Event Detail | poster image at top, status badge overlay, icon-prefixed info rows, description card, side-by-side stats cards, registration date cards, waitlist integrity section, bottom nav |
| Profile | circle avatar with initial letter, stats row (Events/Selected/Waiting), Edit button in header, Delete Profile at bottom, bottom nav |
| Add Event | back arrow + title header, red ADD EVENT banner, sectioned form (BASIC INFO/SCHEDULE/REGISTRATION), Material TextInputLayouts, side-by-side fields, bottom nav |
| Admin | horizontal back arrow + title layout |
| Register | role toggle buttons (Entrant/Organizer/Admin) replacing checkbox |
| Event Cards | MaterialCardView, status badges, location info, waiting/spots counts |

### new drawables
- `bg_circle_avatar.xml`, `bg_status_badge.xml`, `bg_status_waiting.xml`, `bg_status_closed.xml`, `bg_status_selected.xml`
- `bg_chip_selected.xml`, `bg_chip_unselected.xml`, `bg_role_selected.xml`, `bg_role_unselected.xml`
- `bg_add_event_header.xml`

### new colors
- `status_selected` (#2196F3), `status_declined` (#E74C3C)
- `nav_icon_inactive` (#999999), `nav_icon_active` (#C62828), `bottom_nav_bg` (#FAFAFA)

### code cleanup
- removed ALL comments from all 20 Java files
- removed XML comments from layout files
- removed comments from build.gradle.kts

---

## all files changed

**java (20 files):** all files in `com.example.vigilante` — comments removed + UI/logic updates where needed
**layouts:** `homepage.xml`, `allevents.xml`, `activity_event_detail.xml`, `profile_page.xml`, `activity_add_event.xml`, `adminpage.xml`, `register_page.xml`, `item_event.xml`, `item_entrant.xml`, `attendee_list.xml`, `activity_lottery_info.xml`, `item_profile.xml`
**values:** `colors.xml`, `themes.xml`
**drawables:** 10 new drawable XMLs
**menu:** `bottom_nav_menu.xml`
**config:** `AndroidManifest.xml`, `build.gradle.kts`
