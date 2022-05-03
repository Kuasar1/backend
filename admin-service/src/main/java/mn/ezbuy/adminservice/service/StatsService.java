package mn.ezbuy.adminservice.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.adminservice.repository.RatingRepository;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    @Autowired
    private final RatingRepository ratingRepository;

    @Autowired
    private final WriterService writerService;

    private static final String RATINGS_FILE = System.getProperty("user.dir") + "/admin-service/src/main/data/ratings.csv";

    @SneakyThrows
    public ResponseEntity<?> generateRecommendation() {
        try {

            ResponseEntity<?> writeRes = writerService.write();
            if(writeRes.getStatusCode() != HttpStatus.OK) {
                return new ResponseEntity<>("Could not write ratings to a file",HttpStatus.BAD_REQUEST);
            }

            //DataModel model = new FileDataModel(new File(RATINGS_FILE));

            MysqlDataSource dbsource = new MysqlDataSource();
            dbsource.setUser("root");
            dbsource.setPassword("password");
            dbsource.setServerName("localhost");
            dbsource.setDatabaseName("main");

            DataModel model = new MySQLJDBCDataModel(dbsource,
                    "ratings",
                    "user_id",
                    "product_id",
                    "rating",
                    "timestamp");

            PearsonCorrelationSimilarity  dbSim = new PearsonCorrelationSimilarity(model);
            System.out.println("db sim: " + dbSim.userSimilarity(1,137));

            PearsonCorrelationSimilarity cosineSimilarity = new PearsonCorrelationSimilarity(model);
            System.out.println("cosineSimilarity: " + cosineSimilarity.userSimilarity(1,137));

            LogLikelihoodSimilarity likelihoodSimilarity = new LogLikelihoodSimilarity(model);
            System.out.println("likelihoodSimilarity: " + likelihoodSimilarity.userSimilarity(1,137));

            EuclideanDistanceSimilarity euclideanDistanceSimilarity = new EuclideanDistanceSimilarity(model);
            System.out.println("euclideanDistanceSimilarity: " + euclideanDistanceSimilarity);

            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.5,euclideanDistanceSimilarity,model);
            long[] neighbors = neighborhood.getUserNeighborhood(137);
            System.out.println("Neighbors of user 137 are: ");
            for (long user : neighbors) {
                System.out.println("user: " + user);
            }

            UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(model,neighborhood,euclideanDistanceSimilarity);

            List<RecommendedItem> recommendedItems = userBasedRecommender.recommend(137,2);
            System.out.println("Recommendations for user 137 are: ");
            System.out.println("################################");
            for (RecommendedItem item : recommendedItems) {
                long itemId = item.getItemID();
                float estimatedPreference = userBasedRecommender.estimatePreference(1,itemId);
                System.out.println("Item ID: " + itemId + " | pref: " + estimatedPreference);
            }
            System.out.println("");
            System.out.println("");
            System.out.println("");
            long[] userIDs = userBasedRecommender.mostSimilarUserIDs(137,2);
            System.out.println("Most similar users for user 137 are: ");
            for (long id : userIDs) {
                System.out.println("ID: " + id);
            }
            ItemBasedRecommender itemBasedRecommender = new GenericItemBasedRecommender(model,cosineSimilarity);
            List<RecommendedItem> recommendedItemList = itemBasedRecommender.mostSimilarItems(16,2);
            System.out.println("Recommendations for user 16 are: ");
            System.out.println("################################");
            for (RecommendedItem item : recommendedItemList) {
                long itemId = item.getItemID();
                float estimatedPreference = userBasedRecommender.estimatePreference(1,itemId);
                System.out.println("Item ID: " + itemId + " | pref: " + estimatedPreference);
            }


//            ItemSimilarity sim = new LogLikelihoodSimilarity(newmodel);
//
//            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(newmodel,sim);
//
//            int x = 1 ;
//            for(LongPrimitiveIterator items = newmodel.getItemIDs(); items.hasNext();) {
//                long itemId = items.nextLong();
//                List<RecommendedItem> recommenderList = recommender.mostSimilarItems(itemId,5);
//
//                for(RecommendedItem recommendedItem : recommenderList) {
//                    System.out.println(itemId + " | " + recommendedItem.getItemID() + " | " + recommendedItem.getValue());
//                }
//            }
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
