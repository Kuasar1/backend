package mn.ezbuy.recommendationservice.service;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.model.DataModel;

import com.google.common.base.Preconditions;

public final class PearsonCorrelationSimilarity extends AbstractSimilarity {

    public PearsonCorrelationSimilarity(DataModel dataModel) throws TasteException {
        this(dataModel, Weighting.UNWEIGHTED);
    }

    public PearsonCorrelationSimilarity(DataModel dataModel, Weighting weighting) throws TasteException {
        super(dataModel, weighting, true);
        Preconditions.checkArgument(dataModel.hasPreferenceValues(), "DataModel doesn't have preference values");
    }

    @Override
    double computeResult(int n, double sumXY, double sumX2, double sumY2, double sumXYdiff2) {
        if (n == 0) {
            return Double.NaN;
        }
        double denominator = Math.sqrt(sumX2) * Math.sqrt(sumY2);
        if (denominator == 0.0) {
            return Double.NaN;
        }
        return sumXY / denominator;
    }

}

