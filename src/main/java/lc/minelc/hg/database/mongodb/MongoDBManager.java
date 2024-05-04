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

import lc.minelc.hg.others.kits.KitStorage;
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
        final HGPlayerData data =  new HGPlayerData();

        if (document == null) {
            return data;
        }

        data.kills = document.getInteger("kills", 0);
        data.deaths = document.getInteger("deaths", 0);
        data.wins = document.getInteger("wins", 0);
        data.kitSelected = document.getInteger("kit", 0);
        data.level = LevelStorage.getStorage().getLevels(data);

        return data;
    }

    public void saveData(final UUID uuid, final HGPlayerData data) {
        final Document query = new Document();
        query.put("_id", uuid);


        final UpdateOptions options = new UpdateOptions().upsert(true);
        final Bson update = Updates.combine(getInfo(data));

        profiles.updateOne(query, update, options);
    }

    private Bson[] getInfo(final HGPlayerData data) {
        final List<Bson> info = new ArrayList<>();
        if (data.kills != 0) {
            info.add(Updates.set("kills", data.kills));
        }
        if (data.deaths != 0) {
            info.add(Updates.set("deaths", data.deaths));
        }
        if (data.wins != 0) {
            info.add(Updates.set("wins", data.wins));
        }
        if (data.kitSelected != (KitStorage.getStorage().defaultKit() != null ? KitStorage.getStorage().defaultKit().id() : 0)) {
            info.add(Updates.set("kit", data.kitSelected));
        }
        return info.toArray(new Bson[0]);
    }

    static void update(MongoDBManager manager) {
        queryManager = manager;
    }
    
    public static MongoDBManager getManager() {
        return queryManager;
    }
}