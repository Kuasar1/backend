package mn.ezbuy.recommendationservice.service;

import com.google.common.base.Preconditions;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.SamplingLongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public final class ThresholdUserNeighborhood extends AbstractUserNeighborhood {

    private final double threshold;

    public ThresholdUserNeighborhood(double threshold, UserSimilarity userSimilarity, DataModel dataModel) {
        this(threshold, userSimilarity, dataModel, 1.0);
    }

    public ThresholdUserNeighborhood(double threshold,
                                     UserSimilarity userSimilarity,
                                     DataModel dataModel,
                                     double samplingRate) {
        super(userSimilarity, dataModel, samplingRate);
        Preconditions.checkArgument(!Double.isNaN(threshold), "threshold must not be NaN");
        this.threshold = threshold;
    }

    @Override
    public long[] getUserNeighborhood(long userID) throws TasteException {

        DataModel dataModel = getDataModel();
        FastIDSet neighborhood = new FastIDSet();
        LongPrimitiveIterator usersIterable = SamplingLongPrimitiveIterator.maybeWrapIterator(dataModel
                .getUserIDs(), getSamplingRate());
        UserSimilarity userSimilarityImpl = getUserSimilarity();

        while (usersIterable.hasNext()) {
            long otherUserID = usersIterable.next();
            if (userID != otherUserID) {
                double theSimilarity = userSimilarityImpl.userSimilarity(userID, otherUserID);
                if (!Double.isNaN(theSimilarity) && theSimilarity >= threshold) {
                    neighborhood.add(otherUserID);
                }
            }
        }

        return neighborhood.toArray();
    }

    @Override
    public String toString() {
        return "ThresholdUserNeighborhood";
    }

}
