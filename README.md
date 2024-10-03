# 🔥 RaveSQL Documentation 🔥

Welcome, fellow hardcore ravers, to the **RaveSQL** universe! Here, SQL queries and database updates drop like the sickest beats in a non-stop rave, keeping your app’s data in sync with the high-energy BPM of speedcore. 

## Why RaveSQL?

   - **Streamline Database Interactions:** Link your Java methods to SQL scripts seamlessly, reducing boilerplate and enhancing maintainability.
   - **Automatic Result Mapping:** Automatically map SQL query results to your Java objects, making data handling as smooth as your favorite beats.
   - **Comprehensive Querying & Updating:** Access a wide range of methods for querying and updating, ensuring all your data needs are met with high-energy efficiency.
   - **Enhance Performance:** Utilize SQL caching for rapid query access, ensuring your application runs smoothly without missing a beat.
   - **Reliable Error Handling:** Custom exceptions keep your data rave secure by managing errors gracefully.

---
**Hardcore Never Dies!** ⚡

---


## 💥 Table of Contents

1. [SqlPath Annotation](#-the-core-annotation-sqlpath)
2. [The Main Stage: RaveRepository](#-the-main-stage-raverepository)
3. [Method Breakdown](#-method-breakdown)
   - [Query Methods](#-query-methods)
   - [Raw Query Methods](#-raw-query-methods)
   - [Query for Single Object](#-query-for-single-object)
   - [Raw Query for Single Object](#-raw-query-for-single-object)
   - [Update Methods](#-update-methods)
   - [Raw Update Methods](#-raw-update-methods)
   - [Batch Update](#-batch-update)
   - [Raw Batch Update](#-raw-batch-update)
   - [Preload SQL Queries](#-preload-sql-queries)
   - [Clear SQL Cache](#-clear-sql-cache)
4. [Error Handling](#-error-handling)
5. [Examples from the Rave Scene](#-examples-from-the-rave-scene)
6. [Afterparty](#-afterparty)


---

## Drop the Bass with Maven 
---

### 1. Add the RaveSQL Dependency

Just like adding the hottest track to your rave playlist, include RaveSQL in your `pom.xml` to keep the party going:

```xml
<dependency>
    <groupId>com.ravesql</groupId>
    <artifactId>ravesql</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Import RaveSQL into Your Project

Bring in the RaveSQL vibes by importing the necessary classes into your Java files:

```java
import com.ravesql.RaveRepository;
import com.ravesql.annotation.SqlPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
```

### 3. Inject RaveRepository with Spring’s Autowire

Let RaveRepository be the DJ that mixes your data seamlessly. Inject it into your service class using Spring’s `@Autowired` or constructor injection:

```java
@Repository
public class PartyRepository {

    private final RaveRepository raveRepository;

    @Autowired
    public PartyRepository(RaveRepository raveRepository) {
        this.raveRepository = raveRepository;
    }

    @SqlPath("sql/getPartyTracksByGenre.sql")
    public List<Track> getPartyTracks(String genre) {
        return raveRepository.query(Track.class, "genre", genre);
    }
}
```

✨ **Pro Tip:** Use the `@SqlPath` annotation to link your methods to their SQL scripts, ensuring every query hits the dancefloor with precision!

---

## Gradle Integration: Spin Those Beats with build.gradle
---
### 1. Add the RaveSQL Dependency

Inject some hardcore energy into your `build.gradle` by adding RaveSQL to your dependencies:

```groovy
dependencies {
    implementation 'com.ravesql:ravesql:1.0.0'
}
```

### 2. Import RaveSQL into Your Project

Sync your project’s rhythm by importing RaveSQL classes:

```java
import com.ravesql.RaveRepository;
import com.ravesql.annotation.SqlPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
```

### 3. Inject RaveRepository with Spring’s Autowire

Let RaveRepository handle your data flows like a speedcore beat. Inject it into your service class:

```java
@Repository
public class BeatRepository {

    private final RaveRepository raveRepository;

    @Autowired
    public BeatRepository(RaveRepository raveRepository) {
        this.raveRepository = raveRepository;
    }

    @SqlPath("sql/getBeatDetails.sql")
    public BeatDetails getBeatDetails(int beatId) {
        return raveRepository.queryForObject(BeatDetails.class, "id", beatId);
    }
}
```

✨ **Pro Tip:** Keep your SQL scripts ready and let `@SqlPath` guide your methods to execute queries without missing a beat!

---

## Example Usage: Keep the Data Flowing Like a Non-Stop Rave 

Here’s how you can harness the power of RaveSQL in your application:

```java
@Repository
public class TrackRepository {

    private final RaveRepository raveRepository;

    @Autowired
    public TrackRepository(RaveRepository raveRepository) {
        this.raveRepository = raveRepository;
    }

    @SqlPath("sql/getTracks.sql")
    public List<DanceMove> getTracksByStyle(String style) {
        return raveRepository.query(Tracks.class, "style", style);
    }

    @SqlPath("sql/insertTrack.sql")
    public int addTrack(Track track) {
        return raveRepository.update(track);
    }

    @SqlPath("sql/updateTrack.sql")
    public int update(int bpm) {
        return raveRepository.update("bpm", bpm);
    }

    @SqlPath("sql/deleteTrack.sql")
    public int deleteTrack(int trackId) {
        return raveRepository.update("trackId", trackId);
    }
}
```

###  Preload Your SQL Tracks

Before the rave starts, preload your SQL queries to ensure everything runs smoothly:

```java
@Component
public class RaveInitializer {

    private final RaveRepository raveRepository;

    @Autowired
    public RaveInitializer(RaveRepository raveRepository) {
        this.raveRepository = raveRepository;
        preloadQueries();
    }

    private void preloadQueries() {
        raveRepository.preloadSqlQueries(List.of(
            "sql/getTracks.sql",
            "sql/insertTrack.sql",
            "sql/updateTrack.sql",
            "sql/deleteTrack.sql"
        ));
    }
}
```

###  Clear the SQL Cache When Needed

Need to refresh the playlist? Clear the SQL cache effortlessly:

```java
public void refreshPlaylist() {
    raveRepository.clearSqlCache();
    preloadQueries();
}
```

---

## 😈 The Core Annotation: @SqlPath

###  Overview

**`@SqlPath`** is your backstage pass, linking your Java methods to their SQL scripts. Just like a DJ synchronizes tracks to maintain the rave's vibe, `@SqlPath` ensures your SQL queries are always in sync with your application's data flow.

### Usage

```java
import com.ravesql.annotations.SqlPath;

public class TrackService {

    @SqlPath("sql/getTracksByBPM.sql")
    public List<Track> getTracksByBPM(int bpm) {
        // Method implementation
    }
}
```

### Parameters

- **`value`**: The relative path to your SQL file. Think of it as the playlist that keeps the rave alive.

### Example

Imagine you're spinning a set list for a gabber night. Each method in your service class corresponds to a specific SQL script that fetches or manipulates data:

```java
@SqlPath("sql/bringTheHardcore.sql")
public List<Track> bringTheHardcore();
```

---

## 🤘 The Main Stage: `RaveRepository`

**RaveRepository** is the lifeblood of your data interactions, ensuring your application grooves seamlessly with the database. Whether you're querying tracks, updating rave schedules, or managing ravers, RaveRepository keeps everything in harmony with the relentless energy of a hardcore rave.

### Key Features

- **SQL Caching**: Keeps your SQL scripts ready to drop like your favorite tracks.
- **Flexible Querying**: Supports both annotated and raw SQL queries.
- **Batch Operations**: Perform mass updates to keep the rave energy flowing.
- **Error Handling**: Custom exceptions to manage data flow disruptions gracefully.

---

## 💣 Method Breakdown

Let's break down each method in RaveRepository, ensuring you know how to keep your data rave lit!

### Query Methods

**Purpose**: Retrieve lists of objects from the database based on your SQL queries.

#### `query(Class<T> type, Object... keyValues)`

- **Description**: Executes a SQL query linked via `@SqlPath`, using key-value pairs as parameters.
- **Example**:

    ```java
    @SqlPath("sql/getTracksByGenre.sql")
    public List<Track> getTracksByGenre(String genre) {
        return raveRepository.query(Track.class, "genre", genre);
    }
    ```

#### `query(Class<T> type, Object params)`

- **Description**: Executes a SQL query with a parameter object.
- **Example**:

    ```java
    @SqlPath("sql/getRaversByGenreAndCity.sql")
    public List<Raver> getRaversByGenreAndCity(RaverFilter filter) {
        return raveRepository.query(Raver.class, filter);
    }
    ```

#### `query(Class<T> type)`

- **Description**: Executes a SQL query without any parameters.
- **Example**:

    ```java
    @SqlPath("sql/getAllTracks.sql")
    public List<Track> getAllTracks() {
        return raveRepository.query(Track.class);
    }
    ```

---

### Raw Query Methods

**Purpose**: Execute raw SQL queries by specifying the SQL path directly, giving you full control over the data flow.

#### `rawQuery(String sqlPath, Class<T> type, Object... keyValues)`

- **Description**: Executes a raw SQL query with key-value pairs.
- **Example**:

    ```java
    public List<Track> getTracksByGenre(String genre) {
        return raveRepository.rawQuery("sql/getTracksBySpeedcoreBPM.sql", Track.class, "genre", genre);
    }
    ```

#### `rawQuery(String sqlPath, Class<T> type, Object params)`

- **Description**: Executes a raw SQL query with a parameter object.
- **Example**:

    ```java
    public List<Track> searchTracksByArtist(Artist artist) {
        return raveRepository.rawQuery("sql/searchRavers.sql", Track.class, artist);
    }
    ```

#### `rawQuery(String sqlPath, Class<T> type)`

- **Description**: Executes a raw SQL query without any parameters.
- **Example**:

    ```java
    public List<Track> getPopularTracks() {
        return raveRepository.rawQuery("sql/getPopularTracks.sql", Track.class);
    }
    ```

---

### Query for Single Object

**Purpose**: Retrieve a single object from the database that matches the query parameters.

#### `queryForObject(Class<T> type, Object... keyValues)`

- **Description**: Executes a SQL query to fetch a single object using key-value pairs.
- **Example**:

    ```java
    @SqlPath("sql/getTrackById.sql")
    public Raver getTrackById(int id) {
        return raveRepository.queryForObject(Track.class, "id", id);
    }
    ```

#### `queryForObject(Class<T> type, Object params)`

- **Description**: Executes a SQL query to fetch a single object with a parameter object.
- **Example**:

    ```java
    @SqlPath("sql/getFestivalByCity.sql")
    public Festival getFestivalByCity(City city) {
        return raveRepository.queryForObject(Festival.class, city);
    }
    ```

#### `queryForObject(Class<T> type)`

- **Description**: Executes a SQL query to fetch a single object without any parameters.
- **Example**:

    ```java
    @SqlPath("sql/getFeaturedRave.sql")
    public Raver getFeaturedRave() {
        return raveRepository.queryForObject(Rave.class);
    }
    ```

---

### Raw Query for Single Object

**Purpose**: Execute raw SQL queries to retrieve a single object, offering precise control over data selection.

#### `rawQueryForObject(String sqlPath, Class<T> type, Object... keyValues)`

- **Description**: Executes a raw SQL query to fetch a single object using key-value pairs.
- **Example**:

    ```java
    public Raver getRaverByEmail(String email) {
        return raveRepository.rawQueryForObject("sql/getRaverByEmail.sql", Raver.class, "email", email);
    }
    ```

#### `rawQueryForObject(String sqlPath, Class<T> type, Object params)`

- **Description**: Executes a raw SQL query to fetch a single object with a parameter object.
- **Example**:

    ```java
    public Raver getRaverBySocialMediaHandle(SocialMediaHandle handle) {
        return raveRepository.rawQueryForObject("sql/getRaverBySocialMediaHandle.sql", Raver.class, handle);
    }
    ```

#### `rawQueryForObject(String sqlPath, Class<T> type)`

- **Description**: Executes a raw SQL query to fetch a single object without any parameters.
- **Example**:

    ```java
    public Raver getRaveAmbassador() {
        return raveRepository.rawQueryForObject("sql/getRaveAmbassador.sql", Raver.class);
    }
    ```

---

### Update Methods

**Purpose**: Perform update operations on the database, modifying records to keep your data rave synchronized.

#### `update(Object... keyValues)`

- **Description**: Executes an update operation using key-value pairs.
- **Example**:

    ```java
    @SqlPath("sql/updateTrackBPM.sql")
    public int updateTrackBPM(int trackId, int newBPM) {
        return raveRepository.update("trackId", trackId, "newBPM", newBPM);
    }
    ```

#### `update(Object params)`

- **Description**: Executes an update operation with a parameter object.
- **Example**:

    ```java
    @SqlPath("sql/updateRaveInfo.sql")
    public int updateRaverInfo(RaveInfoUpdate raveInfo) {
        return raveRepository.update(raveInfo);
    }
    ```

#### `update()`

- **Description**: Executes an update operation without any parameters.
- **Example**:

    ```java
    @SqlPath("sql/resetDailyMusicStats.sql")
    public int resetDailyMusicStats() {
        return raveRepository.update();
    }
    ```

---

### Raw Update Methods

**Purpose**: Execute raw update operations by specifying the SQL path directly, offering full control over data modifications.

#### `rawUpdate(String sqlPath, Object... keyValues)`

- **Description**: Executes a raw update operation using key-value pairs.
- **Example**:

    ```java
    public int deactivateRaver(int raverId) {
        return raveRepository.rawUpdate("sql/deactivateRaver.sql", "raverId", raverId);
    }
    ```

#### `rawUpdate(String sqlPath, Object params)`

- **Description**: Executes a raw update operation with a parameter object.
- **Example**:

    ```java
    public int updateTrackDetails(TrackUpdate trackUpdate) {
        return raveRepository.rawUpdate("sql/updateTrackDetails.sql", trackUpdate);
    }
    ```

#### `rawUpdate(String sqlPath)`

- **Description**: Executes a raw update operation without any parameters.
- **Example**:

    ```java
    public int archiveOldTracks() {
        return raveRepository.rawUpdate("sql/archiveOldTracks.sql");
    }
    ```

---

### Batch Update

**Purpose**: Perform batch updates to modify multiple records in one synchronized drop, keeping the rave energy high.

#### `batchUpdate(List<?> paramObjects)`

- **Description**: Executes a batch update using a list of parameter objects.
- **Example**:

    ```java
    @SqlPath("sql/updateMultipleRavers.sql")
    public int[] updateAllTracks(List<TrackUpdate> updates) {
        return raveRepository.batchUpdate(updates);
    }
    ```

---

### Raw Batch Update

**Purpose**: Execute raw batch updates with precise control over each data modification.

#### `rawBatchUpdate(String sqlPath, List<?> paramObjects)`

- **Description**: Executes a raw batch update using a list of parameter objects.
- **Example**:

    ```java
    public int[] deactivateMultipleTracks(List<Integer> trackIds) {
        return raveRepository.rawBatchUpdate("sql/deactivateTracks.sql", trackIds);
    }
    ```

---

### Preload SQL Queries

**Purpose**: Preload a list of SQL queries into the cache, ensuring your favorite tracks are always ready to drop without delay.

#### `preloadSqlQueries(List<String> sqlPaths)`

- **Description**: Loads multiple SQL scripts into the cache.
- **Example**:

    ```java
    public void preloadFestivalSql() {
        List<String> sqlTracks = Arrays.asList(
            "sql/getAllRavers.sql",
            "sql/getAllTracks.sql",
            "sql/getAllEvents.sql"
        );
        raveRepository.preloadSqlQueries(sqlTracks);
    }
    ```

---

### Clear SQL Cache

**Purpose**: Clears the SQL cache, allowing you to refresh your playlist and ensure the latest queries are always in sync with your application's needs.

#### `clearSqlCache()`

- **Description**: Empties the SQL cache.
- **Example**:

    ```java
    public void refreshSqlCache() {
        raveRepository.clearSqlCache();
    }
    ```

---

## 💀 Error Handling

At the heart of every rave, there are bouncers ensuring everything runs smoothly. Similarly, **RaveRepository** comes equipped with **`SqlRepositoryException`**, your custom unchecked exception to handle any hiccups during data interactions.

### `SqlRepositoryException`

- **Usage**: Thrown when there's an issue with SQL queries or parameters.
- **Scenarios**:
  - Missing SQL files.
  - Errors reading SQL scripts.
  - Invalid query parameters.

### Example

```java
public Raver getRaverById(int id) {
    try {
        return raveRepository.queryForObject(Raver.class, "id", id);
    } catch (RaveRepository.SqlRepositoryException e) {
        // Handle exception, maybe log the missed beat
        logger.error("Failed to retrieve raver with ID: " + id, e);
        throw e;
    }
}
```

---

## 🎶 Examples from the Rave Scene

Let's translate some real-world rave scenarios into RaveRepository operations!

### Example 1: Fetching Tracks by BPM

**Scenario**: You're curating a setlist for a hardcore rave and need tracks with specific BPMs.

```java
@SqlPath("sql/getTracksByBPM.sql")
public List<Track> getTracksByBPM(int bpm) {
    return raveRepository.query(Track.class, "bpm", bpm);
}
```

**SQL (`getTracksByBPM.sql`)**:

```sql
SELECT * FROM tracks WHERE bpm = :bpm;
```

### Example 2: Updating Festival Information

**Scenario**: A festival updates their profile with new social media handles.

```java
@SqlPath("sql/updateFestivalInfo.sql")
public int updateRaverInfo(FestivalUpdate update) {
    return raveRepository.update(update);
}
```

**SQL (`updateFestivalInfo.sql`)**:

```sql
UPDATE festival
SET twitter = :twitter, instagram = :instagram
WHERE id = :id;
```

### Example 3: Preloading SQL Queries Before the Rave

**Scenario**: Before the rave starts, preload essential SQL scripts to ensure no delays during the event.

```java
public void prepareRaveNight() {
    List<String> sqlTracks = Arrays.asList(
        "sql/getAllRavers.sql",
        "sql/getAllTracks.sql",
        "sql/getAllEvents.sql"
    );
    raveRepository.preloadSqlQueries(sqlTracks);
}
```

---

## 🖤 Afterparty

With **RaveSQL**, your data will always be in sync with the pulsating energy of your app’s backend. Embrace the spirit of PLUR, keep your queries clean, your updates efficient, and your data rave alive. 

Always remember: **Hardcore Never Dies!**

---

**Stay Connected:**

- **GitHub**: [RaveSQL](https://github.com/hardrave/RaveSQL)

Happy Raving! 🎧✨
