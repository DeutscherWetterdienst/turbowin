package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous IMMT loading for the OSM observation map. */
final class OsmImmtMapWorkflow {

  private OsmImmtMapWorkflow() {}

  static void start(OSM owner, processing processingDialog) {
    new SwingWorker<List<String>, Void>() {
      @Override
      protected List<String> doInBackground() throws Exception {
        // NB  first pass: determine the number of immt records in the immt file
        //     because if > 1000 immt records the constructed html file will be cause problems
        // when opened with some browsers (eg Edge)

        boolean doorgaan2 = true;
        List<String> immt_list = new ArrayList<>(); // size is now dynamically
        String record = "";
        int teller1 = 0; // for counting total number of records in immt (first loop)
        int teller2 = -1; // for counting numer of records in immt_list (second loop)

        // first check if there is an immt log source file present (and not empty)
        // String record = "";
        String volledig_path_immt = main.logs_dir + java.io.File.separator + main.IMMT_LOG;

        /// File immt_file = new File(volledig_path_immt);
        // if (immt_file.exists() && immt_file.length() > 0)     // length() in bytes
        // {

        // BufferedReader in = null;
        //
        // try
        // {
        //   in = new BufferedReader(new FileReader(volledig_path_immt));
        //
        //   try (BufferedReader br = new BufferedReader(new FileReader(volledig_path_immt))) {
        //
        //   int teller = 0;
        //   while ((record = in.readLine()) != null)
        //   {
        //      teller++;
        //   }
        //   in.close();
        // }

        try (BufferedReader in =
            new BufferedReader(new FileReader(volledig_path_immt))) // try with resources
        {
          // int teller = 0;
          // while ((record = in.readLine()) != null)
          while ((in.readLine()) != null) {
            teller1++;
          }
        } catch (IOException ex) {
          String info = "Error when opening immt.log " + "(" + ex + ")";
          JOptionPane.showMessageDialog(
              null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          doorgaan2 = false;
        }
        // } // if (immt_file.exists() && immt_file.length() > 0)
        // else
        /// {
        //   String info = "No stored observations (immt.log) found on this computer";
        //   JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME + " error",
        // JOptionPane.WARNING_MESSAGE);
        //  doorgaan2 = false;
        // } // else

        System.out.println("--- IMMT log number of records: " + teller1);

        if (doorgaan2) {
          // 2nd pass:
          // - immt file exits and is not empty!

          // List<String> immt_list = new ArrayList<>();           // size is now dynamically
          // String record = "";

          // first check if there is an immt log source file present (and not empty)
          // String volledig_path_immt = main.logs_dir + java.io.File.separator + main.IMMT_LOG;

          // File immt_file = new File(volledig_path_immt);
          // if (immt_file.exists() && immt_file.length() > 0)     // length() in bytes
          // {
          // BufferedReader in2 = null;

          // try
          // {
          // in = new BufferedReader(new FileReader(volledig_path_immt));

          // int teller2 = 0;

          // trick to display always max the last 1000 records
          if (teller1 > 1000) {
            teller2 = (teller1 - 1000) * -1; // e.g. teller1 = 1700 -> teller2 = -700;
          } else {
            teller2 = -1;
          }
          System.out.println(
              "--- IMMT log displaying from record number: " + Math.abs(teller2 + 1));

          try (BufferedReader in2 =
              new BufferedReader(new FileReader(volledig_path_immt))) // try with resources
          {
            while ((record = in2.readLine()) != null) {
              teller2++;
              if (teller2 >= 0) {
                immt_list.add(teller2, record);
              }
            }

            // display the online/offline map via a web browser
            if (main.OSM_mode.equals(main.OSM_ONLINE_MANUAL)
                || main.OSM_mode.equals(
                    main.OSM_ONLINE_AWS_VISUAL)) // NB OSM_ONLINE_MANUAL is APR inclusive
            {
              owner.OSM_display_IMMT_on_online_map(immt_list);
            } else if (main.OSM_mode.equals(main.OSM_OFFLINE_MANUAL)
                || main.OSM_mode.equals(
                    main.OSM_OFFLINE_AWS_VISUAL)) // NB OSM_ONLINE_MANUAL is APR inclusive
            {
              owner.OSM_display_IMMT_on_offline_map(immt_list);
            }
          } catch (IOException ex) {
            String info = "Error when opening immt.log " + "(" + ex + ")";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          }
        } // if (doorgaan2)

        return immt_list; // no use, but maybe for future use
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        // try
        // {
        //   //
        // http://www.codejava.net/java-core/collections/java-list-collection-tutorial-and-examples
        //   List<String> immt_list = get();
        //
        //   for (String element : immt_list)
        //   {
        //      System.out.println(element);
        //   }
        //
        //   //set_latest_obs_values(latest_dashboard_obs);
        //
        // } // try
        // catch (InterruptedException | ExecutionException ex)
        // {
        //   System.out.println("+++ Error in Function: main_IMMT_on_leaflet_map() [main.java] " +
        // ex);
        // }

        processingDialog
            .dispose(); // NB actually it closes much too early, but also putting the closing
        // statement in another functions (eg in display_IMMT_on_leaflet_map()) ,
        // the result is the same (too early)
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
