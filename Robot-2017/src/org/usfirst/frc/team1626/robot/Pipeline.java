package org.usfirst.frc.team1626.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.objdetect.*;

import edu.wpi.first.wpilibj.vision.VisionPipeline;

/**
* Pipeline
*
* @author GRIP
*/
public class Pipeline implements VisionPipeline {

	//Outputs
		private Size newSizeOutput = new Size();
		private Mat cvGaussianblurOutput = new Mat();
		private Mat cvErodeOutput = new Mat();
		private Mat hsvThresholdOutput = new Mat();
		private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();

		static {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}

		/**
		 * This is the primary method that runs the entire pipeline and updates the outputs.
		 */
		public void process(Mat source0) {
			// Step New_Size0:
			double newSizeWidth = -1.0;
			double newSizeHeight = -1.0;
			newSize(newSizeWidth, newSizeHeight, newSizeOutput);

			// Step CV_GaussianBlur0:
			Mat cvGaussianblurSrc = source0;
			Size cvGaussianblurKsize = newSizeOutput;
			double cvGaussianblurSigmax = 1.0;
			double cvGaussianblurSigmay = 7.0;
			int cvGaussianblurBordertype = Core.BORDER_DEFAULT;
			cvGaussianblur(cvGaussianblurSrc, cvGaussianblurKsize, cvGaussianblurSigmax, cvGaussianblurSigmay, cvGaussianblurBordertype, cvGaussianblurOutput);

			// Step CV_erode0:
			Mat cvErodeSrc = cvGaussianblurOutput;
			Mat cvErodeKernel = new Mat();
			Point cvErodeAnchor = new Point(-1, -1);
			double cvErodeIterations = 3.0;
			int cvErodeBordertype = Core.BORDER_CONSTANT;
			Scalar cvErodeBordervalue = new Scalar(-1);
			cvErode(cvErodeSrc, cvErodeKernel, cvErodeAnchor, cvErodeIterations, cvErodeBordertype, cvErodeBordervalue, cvErodeOutput);

			// Step HSV_Threshold0:
			Mat hsvThresholdInput = cvErodeOutput;
			
	        // Values for reflective tape given the image
			double[] hsvThresholdHue = {89.02877697841726, 129.3174061433447};
			double[] hsvThresholdSaturation = {178.86690647482015, 255.0};
			double[] hsvThresholdValue = {59.62230215827338, 255.0};
	        
			hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);

			// Step Find_Contours0:
			Mat findContoursInput = hsvThresholdOutput;
			boolean findContoursExternalOnly = true;
			findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

		}

		/**
		 * This method is a generated getter for the output of a New_Size.
		 * @return Size output from New_Size.
		 */
		public Size newSizeOutput() {
			return newSizeOutput;
		}

		/**
		 * This method is a generated getter for the output of a CV_GaussianBlur.
		 * @return Mat output from CV_GaussianBlur.
		 */
		public Mat cvGaussianblurOutput() {
			return cvGaussianblurOutput;
		}

		/**
		 * This method is a generated getter for the output of a CV_erode.
		 * @return Mat output from CV_erode.
		 */
		public Mat cvErodeOutput() {
			return cvErodeOutput;
		}

		/**
		 * This method is a generated getter for the output of a HSV_Threshold.
		 * @return Mat output from HSV_Threshold.
		 */
		public Mat hsvThresholdOutput() {
			return hsvThresholdOutput;
		}

		/**
		 * This method is a generated getter for the output of a Find_Contours.
		 * @return ArrayList<MatOfPoint> output from Find_Contours.
		 */
		public ArrayList<MatOfPoint> findContoursOutput() {
			return findContoursOutput;
		}


		/**
		 * Fills a size with given width and height.
		 * @param width the width of the size
		 * @param height the height of the size
		 * @param size the size to fill
		 */
		private void newSize(double width, double height, Size size) {
			size.height = height;
			size.width = width;
		}

		/**
		 * Performs a Gaussian blur on the image.
		 * @param src the image to blur.
		 * @param kSize the kernel size.
		 * @param sigmaX the deviation in X for the Gaussian blur.
		 * @param sigmaY the deviation in Y for the Gaussian blur.
		 * @param borderType pixel extrapolation method.
		 * @param dst the output image.
		 */
		private void cvGaussianblur(Mat src, Size kSize, double sigmaX, double sigmaY,
			int	borderType, Mat dst) {
			if (kSize == null) {
				kSize = new Size(1,1);
			}
			Imgproc.GaussianBlur(src, dst, kSize, sigmaX, sigmaY, borderType);
		}

		/**
		 * Expands area of lower value in an image.
		 * @param src the Image to erode.
		 * @param kernel the kernel for erosion.
		 * @param anchor the center of the kernel.
		 * @param iterations the number of times to perform the erosion.
		 * @param borderType pixel extrapolation method.
		 * @param borderValue value to be used for a constant border.
		 * @param dst Output Image.
		 */
		private void cvErode(Mat src, Mat kernel, Point anchor, double iterations,
			int borderType, Scalar borderValue, Mat dst) {
			if (kernel == null) {
				kernel = new Mat();
			}
			if (anchor == null) {
				anchor = new Point(-1,-1);
			}
			if (borderValue == null) {
				borderValue = new Scalar(-1);
			}
			Imgproc.erode(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
		}

		/**
		 * Segment an image based on hue, saturation, and value ranges.
		 *
		 * @param input The image on which to perform the HSL threshold.
		 * @param hue The min and max hue
		 * @param sat The min and max saturation
		 * @param val The min and max value
		 * @param output The image in which to store the output.
		 */
		private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
		    Mat out) {
			Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
			Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
				new Scalar(hue[1], sat[1], val[1]), out);
		}

		/**
		 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
		 * @param input The image on which to perform the Distance Transform.
		 * @param type The Transform.
		 * @param maskSize the size of the mask.
		 * @param output The image in which to store the output.
		 */
		private void findContours(Mat input, boolean externalOnly,
			List<MatOfPoint> contours) {
			Mat hierarchy = new Mat();
			contours.clear();
			int mode;
			if (externalOnly) {
				mode = Imgproc.RETR_EXTERNAL;
			}
			else {
				mode = Imgproc.RETR_LIST;
			}
			int method = Imgproc.CHAIN_APPROX_SIMPLE;
			Imgproc.findContours(input, contours, hierarchy, mode, method);
		}
}