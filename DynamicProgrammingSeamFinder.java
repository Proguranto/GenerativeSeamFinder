// NOTE
// This doesn't work 100%. It produces the correct result one of the small example images,
//   but not on the larger example image.
//   I'll keep working on it, but I put it on the github in case you want to see my progress.

package seamcarving;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 * @see SeamCarver
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {
    @Override
    public List<Integer> findSeam(Picture picture, EnergyFunction f) {
        //System.out.println(picture.width());
        //System.out.println(picture.height());
        // TODO: Replace with your code
        // this array will get filled in one column at a time
        double[][] pixels = new double[picture.width()][picture.height()];

        // for each pixel in the first column
        for (int y = 0; y < picture.height(); y++) {
            // set its total energy according to the energy function
            pixels[0][y] = f.apply(picture, 0, y);
        }

        // for each column after the first one
        for (int x = 1; x < picture.width(); x++) {
            // for each pixel in that line
            for (int y = 0; y < picture.height(); y++) {
                // find the smallest energy for each connecting pixel from the previous column
                double min_energy = Double.POSITIVE_INFINITY;
                for (int z = -1; z <= 1; z++) {
                    // check that we are in the bounds of the picture
                    boolean in_bounds = y + z >= 0 && y + z < picture.height();
                    if (!in_bounds)
                        continue;
                    double this_energy = f.apply(picture, x - 1, y + z);
                    if (this_energy < min_energy)
                        min_energy = this_energy;
                }
                // the energy of this pixel is added to the smallest incoming energy
                pixels[x][y] = f.apply(picture, x, y) + min_energy;
            }
        }
        //for (int y = 0; y < picture.height(); y++){
        //    System.out.println(y + ", " + pixels[picture.width()-1][y]);
        //}

        // track back though the columns and assemble the return list
        List<Integer> return_list = new ArrayList<>();
        double min_energy = Double.POSITIVE_INFINITY;
        int curr_y = 0;
        // for each pixel in the last column
        for (int y = 0; y < picture.height(); y++) {
            // find the pixel with the smallest total energy
            if (pixels[picture.width() - 1][y] < min_energy) {
                min_energy = pixels[picture.width() - 1][y];
                curr_y = y;
            }
        }
        //System.out.println(curr_y);
        // add that to the return list
        return_list.add(curr_y);
        // for each column backwards not including the first
        for (int x = picture.width() - 1; x > 0; x--) {
            // find connecting incoming pixels
            min_energy = Double.POSITIVE_INFINITY;
            int this_y = -1;
            for (int z = -1; z <= 1; z++) {
                // check that we are in the bounds of the picture
                boolean in_bounds = curr_y + z >= 0 && curr_y + z < picture.height();
                // find the minimum of the incoming pixels
                if (in_bounds && pixels[x - 1][curr_y + z] < min_energy) {
                    min_energy = pixels[x - 1][curr_y + z];
                    this_y = curr_y + z;
                }
            }
            curr_y = this_y;
            // add that to the return list
            return_list.add(curr_y);
        }

        Collections.reverse(return_list);
        return return_list;
    }
}
