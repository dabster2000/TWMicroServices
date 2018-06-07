package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import org.vaadin.addon.leaflet.LImageOverlay;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.viritin.label.MLabel;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by hans on 11/08/2017.
 */
public class ConsultantLocationCardImpl extends ConsultantLocationCardDesign implements Box {

    private static final Logger logger = Logger.getLogger(ConsultantLocationCardImpl.class.getName());

    private int priority;
    private int boxWidth;
    private String name;

    private LMap leafletMap;

    //Map<String, Point> addresses;


    public ConsultantLocationCardImpl(ProjectService projectService, PhotoRepository photoRepository, int priority, int boxWidth, String name) {
        this.priority = priority;
        this.boxWidth = boxWidth;
        this.name = name;
        leafletMap = new LMap();
        leafletMap.setWidth("100%");
        leafletMap.setHeight("400px");
        leafletMap.setCenter(0, 0);
        leafletMap.setZoomLevel(6);
        final LOpenStreetMapLayer osm = new LOpenStreetMapLayer();
        leafletMap.addLayer(osm);


        //addresses = new HashMap<>();

        String clientName = "";
        for (Project project : projectService.findAllByActiveTrueOrderByNameAsc().stream().sorted(Comparator.comparing(o -> o.getClient().getName())).collect(Collectors.toCollection(ArrayList::new))) {
            if(project.getLatitude() == 0.0) continue;
            double lat = project.getLatitude();//55.707043;
            double lon = project.getLongitude(); //12.589604000000008;
            //addresses.put(project.getAddress(), new Point(lat, lon));
            Photo photo = photoRepository.findByRelateduuid(project.getClient().getUuid());
            if(photo==null) continue;
            LImageOverlay imageOverlay = new LImageOverlay(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getPhoto()),
                    project.getUuid()+".jpg"), new Bounds(new Point(lat,lon),new Point(lat+(100.0/100000.0), lon-(400.0/100000.0))));
            imageOverlay.setOpacity(0.9);
            //imageOverlay.setAttribution(project.getName());
            imageOverlay.addClickListener(event -> UI.getCurrent().getNavigator().navigateTo(ProjectManagerView.VIEW_NAME+"/"+project.getUuid()));
            leafletMap.addLayer(imageOverlay);

            getProjectList().setDefaultComponentAlignment(Alignment.TOP_CENTER);
            if(!clientName.equals(project.getClient().getName())) {
                clientName = project.getClient().getName();
                MLabel clientNameLabel = new MLabel(clientName).withStyleName("small dark-green-font");
                getProjectList().addComponents(clientNameLabel);
            }
            Button button = new Button(project.getName());
            button.addStyleName("flat");
            button.setWidth("100%");
            button.addClickListener(event -> leafletMap.flyTo(new Point(lat, lon), 16.0));
            getProjectList().addComponent(button);
        }

        leafletMap.zoomToContent();

/*
        ExternalResource url = new ExternalResource("http://www.lib.utexas.edu/maps/historical/newark_nj_1922.jpg");
        LImageOverlay imageOverlay = new LImageOverlay(new ThemeResource("images/map/companies/appension.png"), new Bounds(new Point(lat,lon),new Point(lat+(100.0/100000.0), lon-(400.0/100000.0))));
        imageOverlay.setOpacity(0.9);
        imageOverlay.setAttribution("University of Texas");
        leafletMap.addLayer(imageOverlay);
        leafletMap.zoomToContent();
*/



        Button flyTo2 = new Button("Fly to Golden Gate");
        flyTo2.addClickListener((Button.ClickListener) event -> leafletMap.flyTo(new Point(37.816304, -122.478543), 9.0));

        this.getIframeHolder().addComponents(leafletMap);
    }

    public void init() {
        //new FeederThread().start();
        /*
        ui.access(() -> {
            while (true) {

            }

        });
        */
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Component getBoxComponent() {
        return this;
    }
/*
    class FeederThread extends Thread {
        @Override
        public void run() {
            try {
                // Update the data for a while
                while (true) {
                    Thread.sleep(10000);

                    ui.access(() -> {
                        for (Point point : addresses.values()) {
                            System.out.println("point = " + point);
                            leafletMap.flyTo(point, 15.0);
                            ui.push();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

}
