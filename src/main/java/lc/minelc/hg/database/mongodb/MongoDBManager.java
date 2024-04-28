package lc.minelc.hg.database.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import lc.minelc.hg.others.levels.LevelStorage;

public final class MongoDBManager {

    private static MongoDBManager queryManager;

    private final MongoCollection<Document> profiles;

    public MongoDBManager(MongoCollection<Document> profiles) {
        this.profiles = profiles;
    }

    public HGPlayerData getData(final UUID uuid) {
        final Document query = new Document();
        query.put("_id", uuid);
        final FindIterable<Document> results = profiles.find(query);
        final Document document = results.first();

        if (document == null) {
            return HGPlayerData.createEmptyData();
        }

        final HGPlayerData data = new HGPlayerData();

        data.kills = document.getInteger("kills", 0);
        data.deaths = document.getInteger("deaths", 0);
        data.wins = document.getInteger("wins", 0);
        data.kitSelected = document.getInteger("kit", 0);
        data.kits = createHashSet(document.getList(data, Integer.class), null);
        data.level = LevelStorage.getStorage().getLevels(data);

        return data;
    }

    public void saveData(final UUID uuid, final HGPlayerData data) {
        final Document query = new Document();
        query.put("_id", uuid);

        final List<Integer> kits = toIntegerArray(data.kits);

        final UpdateOptions options = new UpdateOptions().upsert(true);
        final Bson update = createUpdateQuery(data, kits);

        profiles.updateOne(query, update, options);
    }

    private Bson createUpdateQuery(final HGPlayerData data, final List<Integer> kits) {
        return Updates.combine(
            Updates.set("kills", data.kills),
            Updates.set("deaths", data.deaths),
            Updates.set("wins", data.wins),
            Updates.set("kit", data.kitSelected),
            Updates.set("kits", kits)
        );
    }

    private TIntHashSet createHashSet(final List<Integer> data, final Integer defaultValue) {
        if (data == null) {
            final TIntHashSet set = new TIntHashSet();
            if (defaultValue != null) {
                set.add(defaultValue);
            }
            return set;
        }
        final TIntHashSet values = new TIntHashSet(data.size());
        for (final Integer value : data) {
            if (value != null && value != -1) {
                values.add(value);
            }
        }
        return values;
    }

    private List<Integer> toIntegerArray(final TIntHashSet set) {
        final int size = set.size();
        final List<Integer> list = new ArrayList<>(size);
        final TIntIterator iterator = set.iterator();
        for (int i = 0; i < size; i++) {
            list.add(iterator.next());
        }
        return list;
    }

    static void update(MongoDBManager manager) {
        queryManager = manager;
    }
    
    public static MongoDBManager getManager() {
        return queryManager;
    }
}