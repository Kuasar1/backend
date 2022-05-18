package mn.ezbuy.recommendationservice.service;

import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.impl.common.RefreshHelper;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.google.common.base.Preconditions;

abstract class AbstractSimilarity extends AbstractItemSimilarity implements UserSimilarity {

    private PreferenceInferrer inferrer;
    private final boolean weighted;
    private final boolean centerData;
    private int cachedNumItems;
    private int cachedNumUsers;
    private final RefreshHelper refreshHelper;

    AbstractSimilarity(final DataModel dataModel, Weighting weighting, boolean centerData) throws TasteException {
        super(dataModel);
        this.weighted = weighting == Weighting.WEIGHTED;
        this.centerData = centerData;
        this.cachedNumItems = dataModel.getNumItems();
        this.cachedNumUsers = dataModel.getNumUsers();
        this.refreshHelper = new RefreshHelper(() -> {
            cachedNumItems = dataModel.getNumItems();
            cachedNumUsers = dataModel.getNumUsers();
            return null;
        });
    }

    final PreferenceInferrer getPreferenceInferrer() {
        return inferrer;
    }

    @Override
    public final void setPreferenceInferrer(PreferenceInferrer inferrer) {
        Preconditions.checkArgument(inferrer != null, "inferrer is null");
        refreshHelper.addDependency(inferrer);
        refreshHelper.removeDependency(this.inferrer);
        this.inferrer = inferrer;
    }

    final boolean isWeighted() {
        return weighted;
    }

    abstract double computeResult(int n, double sumXY, double sumX2, double sumY2, double sumXYdiff2);

    @Override
    public double userSimilarity(long userID1, long userID2) throws TasteException {
        DataModel dataModel = getDataModel();
        PreferenceArray xPrefs = dataModel.getPreferencesFromUser(userID1);
        PreferenceArray yPrefs = dataModel.getPreferencesFromUser(userID2);
        int xLength = xPrefs.length();
        int yLength = yPrefs.length();

        if (xLength == 0 || yLength == 0) {
            return Double.NaN;
        }

        long xIndex = xPrefs.getItemID(0);
        long yIndex = yPrefs.getItemID(0);
        int xPrefIndex = 0;
        int yPrefIndex = 0;

        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumY = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        double sumXYdiff2 = 0.0;
        int count = 0;

        boolean hasInferrer = inferrer != null;

        while (true) {
            int compare = Long.compare(xIndex, yIndex);
            if (hasInferrer || compare == 0) {
                double x;
                double y;
                if (xIndex == yIndex) {
                    x = xPrefs.getValue(xPrefIndex);
                    y = yPrefs.getValue(yPrefIndex);
                } else {
                    if (compare < 0) {
                        x = xPrefs.getValue(xPrefIndex);
                        y = inferrer.inferPreference(userID2, xIndex);
                    } else {
                        assert inferrer != null;
                        x = inferrer.inferPreference(userID1, yIndex);
                        y = yPrefs.getValue(yPrefIndex);
                    }
                }
                sumXY += x * y;
                sumX += x;
                sumX2 += x * x;
                sumY += y;
                sumY2 += y * y;
                double diff = x - y;
                sumXYdiff2 += diff * diff;
                count++;
            }
            if (compare <= 0) {
                if (++xPrefIndex >= xLength) {
                    if (hasInferrer) {
                        if (yIndex == Long.MAX_VALUE) {
                            break;
                        }
                        xIndex = Long.MAX_VALUE;
                    } else {
                        break;
                    }
                } else {
                    xIndex = xPrefs.getItemID(xPrefIndex);
                }
            }
            if (compare >= 0) {
                if (++yPrefIndex >= yLength) {
                    if (hasInferrer) {
                        if (xIndex == Long.MAX_VALUE) {
                            break;
                        }
                        yIndex = Long.MAX_VALUE;
                    } else {
                        break;
                    }
                } else {
                    yIndex = yPrefs.getItemID(yPrefIndex);
                }
            }
        }

        double result;
        if (centerData) {
            double meanX = sumX / count;
            double meanY = sumY / count;
            double centeredSumXY = sumXY - meanY * sumX;
            double centeredSumX2 = sumX2 - meanX * sumX;
            double centeredSumY2 = sumY2 - meanY * sumY;
            result = computeResult(count, centeredSumXY, centeredSumX2, centeredSumY2, sumXYdiff2);
        } else {
            result = computeResult(count, sumXY, sumX2, sumY2, sumXYdiff2);
        }

        if (!Double.isNaN(result)) {
            result = normalizeWeightResult(result, count, cachedNumItems);
        }
        return result;
    }

    @Override
    public final double itemSimilarity(long itemID1, long itemID2) throws TasteException {
        DataModel dataModel = getDataModel();
        PreferenceArray xPrefs = dataModel.getPreferencesForItem(itemID1);
        PreferenceArray yPrefs = dataModel.getPreferencesForItem(itemID2);
        int xLength = xPrefs.length();
        int yLength = yPrefs.length();

        if (xLength == 0 || yLength == 0) {
            return Double.NaN;
        }

        long xIndex = xPrefs.getUserID(0);
        long yIndex = yPrefs.getUserID(0);
        int xPrefIndex = 0;
        int yPrefIndex = 0;

        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumY = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        double sumXYdiff2 = 0.0;
        int count = 0;


        while (true) {
            int compare = Long.compare(xIndex, yIndex);
            if (compare == 0) {
                double x = xPrefs.getValue(xPrefIndex);
                double y = yPrefs.getValue(yPrefIndex);
                sumXY += x * y;
                sumX += x;
                sumX2 += x * x;
                sumY += y;
                sumY2 += y * y;
                double diff = x - y;
                sumXYdiff2 += diff * diff;
                count++;
            }
            if (compare <= 0) {
                if (++xPrefIndex == xLength) {
                    break;
                }
                xIndex = xPrefs.getUserID(xPrefIndex);
            }
            if (compare >= 0) {
                if (++yPrefIndex == yLength) {
                    break;
                }
                yIndex = yPrefs.getUserID(yPrefIndex);
            }
        }

        double result;
        if (centerData) {
            double meanX = sumX / (double) count;
            double meanY = sumY / (double) count;
            double centeredSumXY = sumXY - meanY * sumX;
            double centeredSumX2 = sumX2 - meanX * sumX;
            double centeredSumY2 = sumY2 - meanY * sumY;
            result = computeResult(count, centeredSumXY, centeredSumX2, centeredSumY2, sumXYdiff2);
        } else {
            result = computeResult(count, sumXY, sumX2, sumY2, sumXYdiff2);
        }

        if (!Double.isNaN(result)) {
            result = normalizeWeightResult(result, count, cachedNumUsers);
        }
        return result;
    }

    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
        int length = itemID2s.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = itemSimilarity(itemID1, itemID2s[i]);
        }
        return result;
    }

    final double normalizeWeightResult(double result, int count, int num) {
        double normalizedResult = result;
        if (weighted) {
            double scaleFactor = 1.0 - (double) count / (double) (num + 1);
            if (normalizedResult < 0.0) {
                normalizedResult = -1.0 + scaleFactor * (1.0 + normalizedResult);
            } else {
                normalizedResult = 1.0 - scaleFactor * (1.0 - normalizedResult);
            }
        }
        if (normalizedResult < -1.0) {
            normalizedResult = -1.0;
        } else if (normalizedResult > 1.0) {
            normalizedResult = 1.0;
        }
        return normalizedResult;
    }

    @Override
    public final void refresh(Collection<Refreshable> alreadyRefreshed) {
        super.refresh(alreadyRefreshed);
        refreshHelper.refresh(alreadyRefreshed);
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "[dataModel:" + getDataModel() + ",inferrer:" + inferrer + ']';
    }

}
