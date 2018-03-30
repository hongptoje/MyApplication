package com.example.hong.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hong.myapplication.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

/**
친구와 만나기를 하기위한 지도화면
1. 내 위치를 가지고 오기위한 권한을 획득한다.
2. 지도를 그린 후 내와 친구의 위치를 마커로 표시한다.
3. 나와 친구의 위도경도 차이를 이용하여 중간지점의 목적지를 마커로 표시한다.
4. 목적지 주변 500m 범위 안에있는 식당을 검색한다.
5. 검색결과는 클러스터링되어 표시되며 화면을 확대하면 식당 마커가 나타난다.
6. [식당마커]를 클릭하면 마커위에 식당이름이 표시되며 화면하단의 정보창에 식당 주소와 전화번호가 나타난다.
7. [전화번호]를 누르면 바로 전화를 걸 수 있도록 화면이 전환된다.
8. [길찾기 버튼]을 클릭하면 내위치에서 목적지까지 가는 길을 화면에 빨간선으로 표시한다.
9. 목적지까지 거리, 소요시간, 도착시간, 택시요금을 표시한다.
10. 길안내 정보를 표시한다.
11. [버스 아이콘]을 클릭한경우 대중교통 길찾기로 재검색하여 화면에 표시한다.
12. [여기서보자]버튼을 클릭하면 채팅창에 "#여기서보자"라는 메시지가 전송된다.
13. 상대방이 "#여기서보자"를 클릭하면 상대방 위치에서 목적지까지 가는 지도를 표시해준다.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, PlacesListener, DirectionFinderListener, TransitDirectionFinderListener {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION; //GPS access : GPS신호를 통해 위치 정보를 받기위한 권한설정
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION; //Cell_ID/WiFi access : WiFi또는 통신사의 기지국 정보를 통해 위치 정보를 받기위한 권한설정
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 12.5f;
    private static float AUTO_ZOOM;
    private static String TAG = "MapActivity";
    //지도의 북동쪽과 남서쪽 모서리를 설정 : 아래코드는 남서쪽 모서리의 경계가 -40,-168이고 북동쪽 모서리의 경계가 71,136인 지도를 배치
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PLACE_PICKER_REQUEST = 1;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted;
    private AutoCompleteTextView mSearchText;//지도 검색어 입력 textview
    private ImageView mGps;//내위치 아이콘
    private ImageView mInfo;//지도정보 아이콘
    private ImageView mPlacePicker;//placepicker 아이콘
    private CheckBox checkBoxRestaurant;//식당체크 박스
    private CheckBox checkBoxCafe;//카페 체크박스
    private RelativeLayout infoWindow;
    private Button naviButton;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvPhoneNumber;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private LatLng friendLatLng;
    private String friendName;
    private Double receiveLatitude;
    private Double receiveLongitude;
    private List<Marker> previous_marker = new ArrayList<>();
    private String placeType;
    private LatLng myLocation;
    private ClusterManager<StringClusterItem> mClusterManager;
    private MarkerOptions markerOptions;
    private List<Address> clusterAddresses;
    private CustomClusterRenderer renderer;
    private Place myPlace;
    private StringClusterItem mStringClusterItem;
    private LatLng destinationLatlng;//도착지 좌표
    private TextView itemLatitude;
    private TextView itemLongitude;
    private LatLng currentLatLng;
    private LatLng itemLatLng;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private  Double itemLatitudeDouble;
    private Double itemLongitudeDouble;
    private  List<Address> destinationAddress;
    private  List<Address> currentAddress;
    private Double currentLatitude;
    private Double currentLongitude;
    private LatLng coordinatesLatLng;
    private RelativeLayout findDirection;
    private TextView startPoint, endPoint, totalDirection, totalTime, endTime, taxiFare;
    private Button closeDirectionInfo;
    private RecyclerView recyclerView;//길찾기 정보 리사이클러뷰
    private ArrayList<FindDirectionItem> findDirectionList;//리사이클러뷰 아이템
    private FindDirectionAdapter findDirectionAdapter;//어댑터
    private RecyclerView.LayoutManager layoutManager;//레이아웃 매니저
    private ImageView taxiImage, busImage;//교통수단 선택 이미지
    private ImageView comeHere;
    private AlertDialog.Builder dialog;

    //대중교통 길찾기 목록
    private RecyclerView transitRecyclerView;//대중교통 길찾기 리사이클러뷰
    private TransitListAdapter transitListAdapter;//대중교통 길찾기 목록 어댑터
    private ArrayList<TransitListItem> transitList;//대중교통 아이템
    private RecyclerView.LayoutManager transitLayoutManager;
    private String strArrivalTime;

    //대중교통 길찾기 상세정보
    private RecyclerView transitDirectionRecyclerView;
    private TransitDirectionAdapter transitDirectionAdapter;
    private ArrayList<TransitDirectionItem> transitDirectionList;
    private RecyclerView.LayoutManager transitDirectionLayoutManager;
    private Button closeTransitDirectionInfo;
    private RelativeLayout relLayoutTransitDirectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mInfo = (ImageView) findViewById(R.id.place_info);
        mPlacePicker = (ImageView) findViewById(R.id.place_picker);
        checkBoxRestaurant = (CheckBox) findViewById(R.id.checkboxRestaurant);
        checkBoxCafe = (CheckBox) findViewById(R.id.checkboxCafe);
        infoWindow = (RelativeLayout) findViewById(R.id.infoWindow);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        naviButton = (Button) findViewById(R.id.naviButton);
        itemLatitude = (TextView) findViewById(R.id.itemLatitude);
        itemLongitude = (TextView)findViewById(R.id.itemLongitude);
        comeHere = (ImageView)findViewById(R.id.comeHere);
        dialog = new AlertDialog.Builder(MapsActivity.this);

        findDirection = (RelativeLayout)findViewById(R.id.findDirection);
        relLayoutTransitDirectionList = (RelativeLayout)findViewById(R.id.relLayoutTransitDirectionList);
        startPoint = (TextView)findViewById(R.id.startPoint);
        endPoint = (TextView)findViewById(R.id.endPoint);
        totalDirection = (TextView)findViewById(R.id.totalDistance);
        totalTime = (TextView)findViewById(R.id.totalTime);
        endTime = (TextView)findViewById(R.id.endTime);
        taxiFare = (TextView)findViewById(R.id.taxiFare);
        closeDirectionInfo = (Button)findViewById(R.id.closeDirectionInfo);

        //대중교통 길찾기 목록창
        transitRecyclerView = (RecyclerView)findViewById(R.id.transit_recycler_view);
        transitList = new ArrayList<>();
        transitListAdapter = new TransitListAdapter(transitList, MapsActivity.this);
        transitLayoutManager = new LinearLayoutManager(MapsActivity.this);
        transitRecyclerView.setLayoutManager(transitLayoutManager);
        transitRecyclerView.setAdapter(transitListAdapter);

        //대중교통 길찾기 상세정보
        transitDirectionRecyclerView = (RecyclerView)findViewById(R.id.transit_direction_recycler_view);
        closeTransitDirectionInfo = (Button)findViewById(R.id.closeTransitDirectionInfo);
        transitDirectionList = new ArrayList<>();
        transitDirectionAdapter = new TransitDirectionAdapter(transitDirectionList, MapsActivity.this);
        transitDirectionLayoutManager = new LinearLayoutManager(MapsActivity.this);
        transitDirectionRecyclerView.setLayoutManager(transitDirectionLayoutManager);
        transitDirectionRecyclerView.setAdapter(transitDirectionAdapter);

        //길찾기 정보창
        taxiImage = (ImageView)findViewById(R.id.taxiImage);
        busImage = (ImageView)findViewById(R.id.busImage);
        recyclerView = (RecyclerView)findViewById(R.id.direction_recycler_view);
        findDirectionList = new ArrayList<>();
        findDirectionAdapter = new FindDirectionAdapter(findDirectionList, MapsActivity.this);
        layoutManager = new LinearLayoutManager(MapsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(findDirectionAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            //친구의 위치 정보를 가지고옴
            receiveLatitude = intent.getDoubleExtra("receiveLatitude", 37.509773);
            receiveLongitude = intent.getDoubleExtra("receiveLongitude", 127.07572);
            friendLatLng = new LatLng(receiveLatitude, receiveLongitude);
            friendName = intent.getStringExtra("friendName");
            Log.d(TAG, "getIntent: receiveLatitude: " + receiveLatitude + " receiveLongitude: " + receiveLongitude);
        }

        //지도를 사용하기위한 권한을 가져오는 메서드
        getLocationPermission();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void init() {
        Log.d(TAG, "init");
        TMapView tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("e86e9219-ec30-466e-8741-4dc6b9d1d399");



        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, Places.getGeoDataClient(this, null), LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        //지도 검색색
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });

        //화면 우측 상단의 GPS 아이콘을 클릭하면 현재 사용자의 위치를 화면 중앙에 표시함
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click gps icon");
                //현재 사용자의 위치정보를 가져와 해당위치로 화면을 이동시키는 메서드
                getDeviceLocation();
            }
        });

        //화면 왼쪽 상단의 상세보기 아이콘을 클릭하면 현재 마커 위치에 대한 상세정보를 표시함
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        //정보창이 화면에 이미 표시되어있는 경우, 정보창을 숨깁니다.
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        //정보창을 표시합니다.
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick: NullpointException: " + e.getMessage());
                }
            }
        });

        //placePicker 아이콘 클릭 이벤트
        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlacePicker 생성, PlacePicker : 지리적 주소화 현지 사업체에 해당하는 장소를 포함한 지도와 주변 장소목록을 표시하는 UI를 제공하는 서비스
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG, "onClick: GooglePlayServicesRepairableException:" + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG, "onClick: GooglePlayServicesNotAvailableException:" + e.getMessage());
                }
            }
        });
        hideSoftKeyboard();

//        //정보창 닫혔을 때 마커색상 다시 원래대로 돌려놈
//        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
//            @Override
//            public void onInfoWindowClose(Marker marker) {
//                Log.d(TAG, "infoWindowClose");
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//                infoWindow.setVisibility(View.INVISIBLE);
//            }
//        });

        //마커 드래그 리스너
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "onMarkerDragStart");
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "onMarkerDragEnd");
                NRPlace(marker.getPosition(), PlaceType.RESTAURANT);
            }
        });


        mClusterManager = new ClusterManager<>(MapsActivity.this, mMap);
        renderer = new CustomClusterRenderer(MapsActivity.this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        //화면 우측상단의 식당 체크박스 기본값 = 체크되어있음, 클리갛여 식당마커를 표시하거나 숨김
        checkBoxRestaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                renderer = new CustomClusterRenderer(MapsActivity.this, mMap, mClusterManager);
            }
        });

        //화면 우측상단의 카페 체크박스 기본값 = 체크안되어있음, 클릭하여 카페마커를 표시하거나 숨김
        checkBoxCafe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                renderer = new CustomClusterRenderer(MapsActivity.this, mMap, mClusterManager);
            }
        });

        //정보창에서 전화번호 클릭하면 전화걸수 있는 화면으로 넘어감
        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "tvPhoneNumberClick: " + tvPhoneNumber.getText());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvPhoneNumber.getText()));
                startActivity(intent);
            }
        });


        //길찾기 버튼클릭 이벤트, 현재 사용자 위치에서 선택한 목적지까지 길찾기 제공
        naviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (transitRecyclerView.getVisibility()==View.VISIBLE){
                    transitRecyclerView.setVisibility(View.INVISIBLE);
                }
                if (recyclerView.getVisibility()==View.INVISIBLE){
                    recyclerView.setVisibility(View.VISIBLE);
                }

                if (findDirectionList.size()>0){
                    findDirectionList.clear();
                    findDirectionAdapter.notifyDataSetChanged();
                }

                //기존에 폴리라인이 있는경우 지움
                if (polylinePaths != null){
                    for (Polyline line : polylinePaths){
                        line.remove();
                    }
                    polylinePaths.clear();
                    Log.d(TAG, "polyLineClear");
                }

                //정보창이 띄워져있는경우 비활성화시킴
                if (infoWindow.getVisibility() == View.VISIBLE){
                    infoWindow.setVisibility(View.INVISIBLE);
                }

                Log.d(TAG, "naviButtonClick: destinationLatitude: "+itemLatitude.getText());
                Log.d(TAG, "naviButtonClick: destinationLongitude: "+itemLongitude.getText());
                Log.d(TAG, "naviButtonClick: currentLatLng: "+currentLatLng);
                itemLatitudeDouble = Double.parseDouble(itemLatitude.getText().toString());
                itemLongitudeDouble = Double.parseDouble(itemLongitude.getText().toString());
                itemLatLng = new LatLng(itemLatitudeDouble, itemLongitudeDouble);
                //사용자위치에서 목적지까지 가는 길을 폴리라인으로 표시해줌
//                mMap.addPolyline(new PolylineOptions().add(currentLatLng, itemLatLng).width(10).color(Color.RED));

                //현재사용자와 목적지 길찾기를 볼수 있도록 카메라를 이동
                LatLngBounds naviBound = new LatLngBounds.Builder()
                        .include(currentLatLng)
                        .include(itemLatLng)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(naviBound, 250));
                sendRequest();
            }
        });

        //길안내 정보창 닫기
        closeDirectionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDirection.setVisibility(View.INVISIBLE);
            }
        });

        //대중교통 길찾기 상세정보창 닫기
        closeTransitDirectionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    relLayoutTransitDirectionList.setVisibility(View.INVISIBLE);
            }
        });

        //길찾기 교통수단 선택
        //기본 - 자가용,택시
        taxiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //지금까지한게 이거임
            }
        });
        //대중교통 길찾기
        busImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //클릭하면 택시 길찾기 한거 라인 지워지고 길찾기 안내창 꺼지고 대중교통 길찾기 한다음에 다시 띄워줘야됨 ㅎㅎ;;

                if (transitList.size()>0){
                    transitList.clear();
                    transitListAdapter.notifyDataSetChanged();
                }

                //기존에 라인 있으면 지움
                if (polylinePaths != null){
                    for (Polyline line : polylinePaths){
                        line.remove();
                    }
                    polylinePaths.clear();
                    Log.d(TAG, "polyLineClear");
                }

                Log.d(TAG, "busImageClick");
//                transitSendRequest();
                Log.d(TAG, "ODsayServiceStart");
                ODsayService oDsayService = ODsayService.init(MapsActivity.this, "qZ53QAfoVn+Qbb1virh0sq2k8r0TLHSIlOqsVDTIypw");
                oDsayService.setReadTimeout(5000);
                oDsayService.setConnectionTimeout(5000);
                OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
                    @Override
                    public void onSuccess(ODsayData oDsayData, API api) {
                            // API Value 는 API 호출 메소드 명을 따라갑니다.
                            if (api == API.SEARCH_PUB_TRANS_PATH) {
//                                List<TransitListItem> transitRouteList = new ArrayList<TransitRoute>();
                                String JSonresult = oDsayData.getJson().toString();
                                JSONObject jsonObject;
                                JSONObject result;
                                JSONArray path;
                                JSONObject jsonPath;
                                JSONObject jsonSubPath;

                                Log.d(TAG, "ODsayJSonresult: "+JSonresult);
                                try {
                                    jsonObject = new JSONObject(JSonresult);
                                     result = jsonObject.getJSONObject("result");
                                     path = result.getJSONArray("path");
                                    for (int i=0; i<path.length(); i++){
                                         jsonPath = path.getJSONObject(i);
                                        JSONArray subPath = jsonPath.getJSONArray("subPath");
                                         for (int j=0; j<subPath.length(); j++) {
                                             jsonSubPath = subPath.getJSONObject(j);
                                             int trafficType = jsonSubPath.getInt("trafficType");
                                             if (trafficType==1){//지하철
                                                String startName = jsonSubPath.getString("startName");
                                                int startExitNo = jsonSubPath.getInt("startExitNo");
                                                String endName = jsonSubPath.getString("endName");
                                                int endExitNo = jsonSubPath.getInt("endExitNo");
                                                int distance = jsonSubPath.getInt("distance");
                                                int sectionTime = jsonSubPath.getInt("sectionTime");
                                                 Log.d(TAG, "jsonSubPath: trafficType == 1: startName: "+startName+" ,startExitNo: "+startExitNo+" ,endName: "+endName+" ,endExitNo:  "+endExitNo+" ,distance: "+distance+" ,sectionTime: "+sectionTime);
                                             }else if (trafficType==2){//버스
                                                 String startName = jsonSubPath.getString("startName");
                                                 String endName = jsonSubPath.getString("endName");
                                                 int distance = jsonSubPath.getInt("distance");
                                                 int sectionTime = jsonSubPath.getInt("sectionTime");
                                                 Log.d(TAG, "jsonSubPath: trafficType == 2: startName: "+startName+" ,endName: "+endName+" ,distance: "+distance+" ,sectionTime: "+sectionTime);
                                             }else if (trafficType==3){//도보
                                                int distance = jsonSubPath.getInt("distance");
                                                 int sectionTime = jsonSubPath.getInt("sectionTime");
                                                Log.d(TAG, "jsonSubPath: trafficType == 3: distance"+distance+" ,sectionTime: "+sectionTime);
                                             }
                                         }

                                         //대중교통 길찾기 정보_총시간, 도보시간, 도착시간, 출발지점, 도착지점
                                        JSONObject info = jsonPath.getJSONObject("info");
                                        String mapObj = info.getString("mapObj");
                                        int busStationCount = info.getInt("busStationCount");
                                        int subwayStationCount = info.getInt("subwayStationCount");
                                        int payment = info.getInt("payment");
                                        int totalTime = info.getInt("totalTime");
                                        int trafficDistance = info.getInt("trafficDistance");
                                        String firstStartStation = info.getString("firstStartStation");
                                        String lastEndStation = info.getString("lastEndStation");
                                        int totalWalk = info.getInt("totalWalk");

                                        double distance = trafficDistance/1000;
                                        int totalWalkMin = totalWalk/60;

                                        //도착시간 구하기
                                        long now = System.currentTimeMillis();
                                        Date date = new Date(now);
                                        //SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
                                        SimpleDateFormat sdfMin = new SimpleDateFormat("mm");
                                        int nowHour = Integer.parseInt(sdfHour.format(date));
                                        int nowMin = Integer.parseInt(sdfMin.format(date));
                                        if ((nowMin+totalTime)>60){
                                           strArrivalTime = "도착시간: "+(nowHour+1)+"시 "+(nowMin+totalTime-60)+"분";
                                        }else{
                                            strArrivalTime = "도착시간: "+nowHour+"시 "+ (nowMin+totalTime)+"분";
                                        }
                                        String strTotalTime = totalTime+"분";
                                        String strPayment = "요금: "+ payment+"원";
                                        String strTotalWalk = "도보: " +totalWalkMin+"분";
                                        String strTrafficDistance ="거리: " +String.format("%.1f", distance)+"Km";
                                        String strBusStataionCount = "버스: "+ busStationCount+" 정거장";
                                        String strSubwayStationCount = "지하철: " + subwayStationCount+" 정거장";
                                        String strFirstStartStation = "출발:"+firstStartStation;
                                        String strLastEndStation = "도착: "+ lastEndStation;

                                        Log.d(TAG, "노선그래픽 데이터 검색: "+mapObj);

                                        Log.d(TAG, "ODsayService: busStationCount: "+busStationCount+" subwayStationCount: "+subwayStationCount+" totalTime: "+totalTime+" trafficDistance: "+trafficDistance+" firstStartStation: "+firstStartStation+" lastEndStation: "+lastEndStation);
                                        transitList.add(new TransitListItem(strTotalTime, strTotalWalk, strTrafficDistance,strFirstStartStation,strLastEndStation, strBusStataionCount, strSubwayStationCount, strPayment, mapObj,strArrivalTime, jsonPath.toString()));
                                        transitListAdapter.notifyDataSetChanged();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        recyclerView.setVisibility(View.INVISIBLE);
                        transitRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(int i, String s, API api) {
                        if (api == API.SEARCH_PUB_TRANS_PATH) {}
                    }
                };
                oDsayService.requestSearchPubTransPath(String.valueOf(currentLongitude), String.valueOf(currentLatitude), String.valueOf(itemLongitudeDouble), String.valueOf(itemLatitudeDouble),"","","",onResultCallbackListener);

            }
        });

        //리사이클러 뷰 터치이벤트에서 손가락을 땔때만 작동하도록 하는 제스터디텍터추가
        final GestureDetector gestureDetector = new GestureDetector(MapsActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        //대중교통 아이템 클릭 이벤트
        transitRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                Log.d(TAG, "onInterceptTouchEvent");
                View child = transitRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)){

                    if (relLayoutTransitDirectionList.getVisibility()==View.INVISIBLE){
                        relLayoutTransitDirectionList.setVisibility(View.VISIBLE);
                    }

                    if (transitDirectionList.size()>0){
                        transitDirectionList.clear();
                        transitDirectionAdapter.notifyDataSetChanged();
                    }

                    if (polylinePaths != null){
                        for (Polyline line : polylinePaths){
                            line.remove();
                        }
                        polylinePaths.clear();
                        Log.d(TAG, "polyLineClear");
                    }

                    Log.d(TAG, "getChildAdapterPosition: "+ transitRecyclerView.getChildAdapterPosition(child));
                    Log.d(TAG, "getChildLayoutPosition: "+ transitRecyclerView.getChildLayoutPosition(child));
                    Log.d(TAG, "getChildViewHolder: "+ transitRecyclerView.getChildViewHolder(child));
                    TextView mapObj = (TextView) transitRecyclerView.getChildViewHolder(child).itemView.findViewById(R.id.mapObj);
                    TextView jsonInfo = (TextView)transitRecyclerView.getChildViewHolder(child).itemView.findViewById(R.id.jsonInfo);
                    Log.d(TAG, "trafficTypeList.getText().toString(): "+jsonInfo.getText().toString());
                    String strSectionTiem;
                    double doubleDistance;
                    String transitSubTitle;
                    String transitTitle;
                    String busNo = null;
                    String subwayCode = null;
                    polylinePaths = new ArrayList<>();
                    LatLng latLng;
                    LatLng latLng2;

                    try {
                        JSONObject jsonObject = new JSONObject(jsonInfo.getText().toString());
                        JSONArray subPath = jsonObject.getJSONArray("subPath");

                        for (int r=0; r<subPath.length(); r++){
                            JSONObject jsonSubpath = subPath.getJSONObject(r);
                            int trafficType = jsonSubpath.getInt("trafficType");
                            if (trafficType == 1 || trafficType ==2) {
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .geodesic(true)
                                        .color(Color.MAGENTA)
                                        .width(10)
                                        .add(currentLatLng);
                                JSONObject passStopList = jsonSubpath.getJSONObject("passStopList");
                                JSONArray stations = passStopList.getJSONArray("stations");
                                for (int k=0; k<stations.length(); k++){
                                    JSONObject jsonStations = stations.getJSONObject(k);
                                    double x = jsonStations.getDouble("x");
                                    double y = jsonStations.getDouble("y");
                                    Log.d(TAG, "jsonStations: trafficTypeSub,Bus: polyLineAllPath: x: "+x+", y: "+y);
                                    latLng = new LatLng(y,x);
                                    polylineOptions.add(latLng);
                                }
                                polylineOptions.add(itemLatLng);
                                polylinePaths.add(mMap.addPolyline(polylineOptions));
                            }

                        }
                        for (int i=0; i<subPath.length(); i++){
                            JSONObject jsonSubPath = subPath.getJSONObject(i);

//                            if (i==1){
//                                PolylineOptions polylineOptions = new PolylineOptions()
//                                        .geodesic(true)
//                                        .color(Color.MAGENTA)
//                                        .width(10);
//                                JSONObject passStopList = jsonSubPath.getJSONObject("passStopList");
//                                JSONArray stations = passStopList.getJSONArray("stations");
//
//                                    JSONObject jsonStations = stations.getJSONObject(0);
//                                    double x = jsonStations.getDouble("x");
//                                    double y = jsonStations.getDouble("y");
//                                    Log.d(TAG, "jsonStations: trafficTypeSub: index=1: x: "+x+", y: "+y);
//                                    latLng = new LatLng(y,x);
//                                    polylineOptions.add(currentLatLng).add(latLng);
//
//                                polylinePaths.add(mMap.addPolyline(polylineOptions));
//                            }

//                            Log.d(TAG, "flowCheck: No.1");
//                            PolylineOptions polylineOptions2 = new PolylineOptions()
//                                    .geodesic(true)
//                                    .color(Color.MAGENTA)
//                                    .width(10)
//                                    .add(currentLatLng);
//                            Log.d(TAG, "flowCheck: No.2");
//                            JSONObject passStopList2 = jsonSubPath.getJSONObject("passStopList");
//                            JSONArray stations2 = passStopList2.getJSONArray("stations");
//                            Log.d(TAG, "testtttttttttttttttttttttt:" +passStopList2.toString());
//                            if (stations2 != null && passStopList2 !=null) {
//                                Log.d(TAG, "flowCheck: No.3");
//                                for (int z = 0; z < stations2.length(); z++) {
//                                    JSONObject jsonStations = stations2.getJSONObject(z);
//                                    double x = jsonStations.getDouble("x");
//                                    double y = jsonStations.getDouble("y");
//                                    Log.d(TAG, "jsonStations: trafficTypeSub: x: " + x + ", y: " + y);
//                                    latLng = new LatLng(y, x);
//                                    polylineOptions2.add(latLng);
//                                }
//                                Log.d(TAG, "flowCheck: No.4");
//                                polylineOptions2.add(itemLatLng);
//                                polylinePaths.add(mMap.addPolyline(polylineOptions2));
//                            }

//                            else if (i==(subPath.length()-1)){
//                                PolylineOptions polylineOptions = new PolylineOptions()
//                                        .geodesic(true)
//                                        .color(Color.MAGENTA)
//                                        .width(10);
//                                JSONObject passStopList = jsonSubPath.getJSONObject("passStopList");
//                                JSONArray stations = passStopList.getJSONArray("stations");
//
//                                JSONObject jsonStations = stations.getJSONObject(0);
//                                double x = jsonStations.getDouble("x");
//                                double y = jsonStations.getDouble("y");
//                                Log.d(TAG, "jsonStations: trafficTypeSub: x: "+x+", y: "+y);
//                                latLng = new LatLng(y,x);
//                                polylineOptions.add(itemLatLng).add(latLng);
//
//                                polylinePaths.add(mMap.addPolyline(polylineOptions));
//                            }

                            int trafficType = jsonSubPath.getInt("trafficType");
                            if (trafficType==1){//지하철
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .geodesic(true)
                                        .color(Color.GREEN)
                                        .width(10);

                                JSONArray lane = jsonSubPath.getJSONArray("lane");
                                for (int j=0; j<lane.length(); j++){
                                    JSONObject jsonLane = lane.getJSONObject(j);
                                    subwayCode = jsonLane.getString("subwayCode");
                                }
//                                 passStopList = jsonSubPath.getJSONObject("passStopList");
//                                 stations = passStopList.getJSONArray("stations");
                                JSONObject passStopList = jsonSubPath.getJSONObject("passStopList");
                                JSONArray stations = passStopList.getJSONArray("stations");
                                for (int k=0; k<stations.length(); k++){
                                    JSONObject jsonStations = stations.getJSONObject(k);
                                    double x = jsonStations.getDouble("x");
                                    double y = jsonStations.getDouble("y");
                                    Log.d(TAG, "jsonStations: trafficTypeSub: x: "+x+", y: "+y);
                                    latLng = new LatLng(y,x);
                                    polylineOptions.add(latLng);
                                }
                                polylinePaths.add(mMap.addPolyline(polylineOptions));

                                String startName = jsonSubPath.getString("startName");
                                int startExitNo = jsonSubPath.getInt("startExitNo");
                                String endName = jsonSubPath.getString("endName");
                                int endExitNo = jsonSubPath.getInt("endExitNo");
                                int distance = jsonSubPath.getInt("distance");
                                int sectionTime = jsonSubPath.getInt("sectionTime");
                                int stationCount = jsonSubPath.getInt("stationCount");

                                transitTitle = subwayCode+"호선 "+startName+"역 승차, "+ endName+"역 하차";
                                transitSubTitle = stationCount+"개 역 이동, "+"출구 : "+endExitNo+"번 출구";

//                                if (distance>=1000) {
//                                    doubleDistance = distance / 1000;
//                                    transitSubTitle = "지하철 "+String.format("%.1f", doubleDistance) + "Km";
//                                }else{
//                                    transitSubTitle = "지하철 "+distance +"m";
//                                }

                                strSectionTiem = sectionTime+"분";
                                Log.d(TAG, "jsonSubPath: trafficType == 1: startName: "+startName+" ,startExitNo: "+startExitNo+" ,endName: "+endName+" ,endExitNo:  "+endExitNo+" ,distance: "+distance+" ,sectionTime: "+sectionTime);
                                transitDirectionList.add(new TransitDirectionItem(R.drawable.sub_green, transitTitle,transitSubTitle,strSectionTiem));
                                transitDirectionAdapter.notifyDataSetChanged();
                            }else if (trafficType==2){//버스
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .geodesic(true)
                                        .color(Color.BLUE)
                                        .width(10);

                                JSONArray lane = jsonSubPath.getJSONArray("lane");
                                for (int j=0; j<lane.length(); j++){
                                    JSONObject jsonLane = lane.getJSONObject(j);
                                    busNo = jsonLane.getString("busNo");
                                }
//                                 passStopList = jsonSubPath.getJSONObject("passStopList");
//                                stations = passStopList.getJSONArray("stations");
                                JSONObject passStopList = jsonSubPath.getJSONObject("passStopList");
                                JSONArray stations = passStopList.getJSONArray("stations");
                                for (int k=0; k<stations.length(); k++){
                                    JSONObject jsonStations = stations.getJSONObject(k);
                                    double x = jsonStations.getDouble("x");
                                    double y = jsonStations.getDouble("y");
                                    Log.d(TAG, "jsonStations: trafficTypeBus: x: "+x+", y: "+y);
                                    latLng = new LatLng(y,x);
                                    polylineOptions.add(latLng);
                                }
                                polylinePaths.add(mMap.addPolyline(polylineOptions));

                                String startName = jsonSubPath.getString("startName");
                                String endName = jsonSubPath.getString("endName");
                                int distance = jsonSubPath.getInt("distance");
                                int sectionTime = jsonSubPath.getInt("sectionTime");
                                int stationCount = jsonSubPath.getInt("stationCount");

                                transitTitle = busNo+"번 버스, "+startName+"승차, "+endName+"하차";
                                transitSubTitle = stationCount+"개 정류장 이동";

//                                if (distance>=1000) {
//                                    doubleDistance = distance / 1000;
//                                    transitSubTitle = "버스 "+String.format("%.1f", doubleDistance) + "Km";
//                                }else{
//                                    transitSubTitle = "버스 "+distance +"m";
//                                }

                                strSectionTiem = sectionTime+"분";
                                Log.d(TAG, "jsonSubPath: trafficType == 2: startName: "+startName+" ,endName: "+endName+" ,distance: "+distance+" ,sectionTime: "+sectionTime);
                                transitDirectionList.add(new TransitDirectionItem(R.drawable.bus_blue, transitTitle,transitSubTitle,strSectionTiem));
                                transitDirectionAdapter.notifyDataSetChanged();
                            }else if (trafficType==3){//도보

                                int distance = jsonSubPath.getInt("distance");
                                int sectionTime = jsonSubPath.getInt("sectionTime");

                                if (distance>=1000) {
                                    doubleDistance = distance / 1000;
                                    transitTitle = "도보 "+String.format("%.1f", doubleDistance) + "Km";
                                }else{
                                    transitTitle = "도보 "+distance +"m";
                                }

                                strSectionTiem = sectionTime+"분";
                                Log.d(TAG, "jsonSubPath: trafficType == 3: distance"+distance+" ,sectionTime: "+sectionTime);
                                transitDirectionList.add(new TransitDirectionItem(R.drawable.walk_puple, transitTitle,"",strSectionTiem));
                                transitDirectionAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

//                    Log.d(TAG, "getChildViewHolder: MapObj: "+mapObj.getText().toString());
//                    String strMapObj = mapObj.getText().toString();
//                    StringTokenizer tokenizer = new StringTokenizer(strMapObj, "@");
//                    String mapObjNO = tokenizer.nextToken();
//                    String mapObjOK = tokenizer.nextToken();
//                    String fixMapObj = "0:0@"+mapObjOK;
//                    Log.d("MapAcTransitListAdapter","mapObjInfo: mapObjNO: "+mapObjNO+", mapObjOK: "+mapObjOK+" ,fixMapObj: "+fixMapObj);

                    //loadLane;;;;;
//                    ODsayService oDsayService = ODsayService.init(MapsActivity.this, "qZ53QAfoVn+Qbb1virh0sq2k8r0TLHSIlOqsVDTIypw");
//                     oDsayService.setReadTimeout(5000);
//                      oDsayService.setConnectionTimeout(5000);
//                        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
//                @Override
//                public void onSuccess(ODsayData oDsayData, API api) {
//
//
//                    if (api == API.LOAD_LANE){
//                        String JSonresult = oDsayData.getJson().toString();
//                        Log.d("MapAcTransitListAdapter","ODsayJSonresult: LOAD_LANE: "+JSonresult);
//                        PolylineOptions polylineOptions = new PolylineOptions()
//                                .geodesic(true)
//                                .color(Color.BLUE)
//                                .width(10);
//                        try {
//                            JSONObject jsonObject = new JSONObject(JSonresult);
//                            JSONObject result = jsonObject.getJSONObject("result");
//                            JSONArray lane = result.getJSONArray("lane");
//                            for (int i=0; i<lane.length(); i++){
//                                JSONObject jsonLane = lane.getJSONObject(i);
//                                JSONArray section = jsonLane.getJSONArray("section");
//                                for (int j=0; j<section.length(); j++){
//                                    JSONObject jsonSection = section.getJSONObject(j);
//                                    JSONArray graphPos = jsonSection.getJSONArray("graphPos");
//                                    for (int k=0; k<graphPos.length(); k++){
//                                        JSONObject jsonGraphPos = graphPos.getJSONObject(k);
//                                        String x = jsonGraphPos.getString("x");
//                                        String y = jsonGraphPos.getString("y");
//                                        LatLng transitLatLng = new LatLng(Double.valueOf(y),Double.valueOf(x));
//                                        Log.d("MapAcTransitListAdapter","xInfo: "+x);
//                                        Log.d("MapAcTransitListAdapter","yInfo: "+y);
//                                        polylineOptions.add(transitLatLng);
//                                    }
//                                }
//                            } polylinePaths.add(mMap.addPolyline(polylineOptions));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onError(int i, String s, API api) {
//                    if (api == API.LOAD_LANE){}
//                }
//            };
//            oDsayService.requestLoadLane(fixMapObj, onResultCallbackListener);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        //여기로와 버튼 클릭이벤트
        comeHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("선택한 목적지로 요청을 전송하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("comeHereLatitude",itemLatitudeDouble);
                        intent.putExtra("comeHereLongitude",itemLongitudeDouble);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

//    //google direction api 할때 썼던 코드
//    private void sendRequest(){
//        Log.d(TAG, "sendRequest");
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
//        try {
//            destinationAddress = geocoder.getFromLocation(itemLatitudeDouble, itemLongitudeDouble,1);
//            currentAddress = geocoder.getFromLocation(currentLatitude, currentLongitude,1);
//            if (destinationAddress.size()>0) {
//                String stringDestinationAddress = destinationAddress.get(0).getAddressLine(0).toString();
//                if (currentAddress.size()>0){
//                    String stringCurrentAddress = currentAddress.get(0).getAddressLine(0).toString();
//                    new DirectionFinder(this, stringDestinationAddress, stringCurrentAddress).executes();
//                    Log.d(TAG, "sendRequest: DirectionFinder: ");
//                    Log.d(TAG, "currentAddress: "+stringCurrentAddress);
//                    Log.d(TAG, "destinationAddress: " + stringDestinationAddress);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void sendRequest(){
        Log.d(TAG, "sendRequest");
        try {
                    new DirectionFinder(this, itemLatitudeDouble, itemLongitudeDouble, currentLatitude, currentLongitude).executes();
                    Log.d(TAG, "sendRequest: DirectionFinder:");
                    Log.d(TAG, "startLatLng: Longitude: "+currentLongitude+" Listitude: "+currentLatitude);
                    Log.d(TAG, "endLatLng: Longitude: " +itemLongitudeDouble+" Listitude: "+itemLatitudeDouble );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transitSendRequest(){
        try{
            new TransitDirectionFinder(this, currentLongitude, currentLatitude, itemLongitudeDouble, itemLatitudeDouble).transitExcute();
            Log.d(TAG, "transitSendRequest: TransitDirectionFinder");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //PlacePicker 클릭 결과 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    //지도검색을 한 경우 불러오는 메서드 주소, 지명을 좌표(위도, 경도)로 변환한 후 해당 위치로 화면을 이동
    private void geoLocate(){
        Log.d(TAG, "geoLocate");
        String searchString = mSearchText.getText().toString();

        //지오코더 객체생성
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            //주소, 지명을 통해 좌표로 변환, 좌표를 주소나 지명으로 변환하는 경우에는 getFromLocation() 메서드를 사용
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.d(TAG, "goeLocate IOException");
        }

        if (list.size() >0 ){
            Address address = list.get(0);
            Log.d(TAG, "geoLocae: foundLocation: "+ address.toString());

            //결과 위치로 화면을 이동, getCountryName(), getLatitude(), getLongitude(), getPostalCode(), getPhone()등의 메서드를 통해 세부정보 추출가능
           moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    //사용자의 위치를 화면에 표시
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete");
                            Location currentLocation = (Location) task.getResult();
                            currentLatitude = currentLocation.getLatitude();
                            currentLongitude = currentLocation.getLongitude();
                            //목적지 위도, 경도
                            destinationLatitude = currentLatitude-(currentLatitude-receiveLatitude)/2;
                            destinationLongitude = currentLongitude-(currentLongitude-receiveLongitude)/2;
                            destinationLatlng = new LatLng(destinationLatitude, destinationLongitude);

                            //사용자 위치로 카메라를 이동 title을 "My Location"으로 설정
                            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                            moveCamera(destinationLatlng, DEFAULT_ZOOM, "My Location");

                            //친구 마커옵션
                            MarkerOptions options = new MarkerOptions()
                                    .position(friendLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon4))
                                    .title(friendName);
                            //내 마커옵션
                            MarkerOptions options2 = new MarkerOptions()
                                    .position(currentLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon3))
                                    .title("나");
                            //목적지 마커옵션
                            MarkerOptions options3 = new MarkerOptions()
                                    .position(destinationLatlng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_flag))
                                    .title("목적지")
                                    .draggable(true);
                            Log.d(TAG,"LatLng: myLatLng: "+currentLocation.getLatitude()+", "+currentLocation.getLongitude());
                            Log.d(TAG,"LatLng: friendLatLng: "+receiveLatitude+", "+receiveLongitude);
                            Log.d(TAG,"LatLng: destinationLatLng: "+destinationLatitude+", "+destinationLongitude);

                            //지도가 보여질 경계 범위를 설정함, currentLatLng과 friendLatLng을 화면안에 포함시킴킴
                           LatLngBounds mLatLngBounds = new LatLngBounds.Builder()
                                    .include(currentLatLng)
                                    .include(friendLatLng)
                                    .build();

                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mLatLngBounds, 300));

                            mMap.clear();
//                            if (previous_marker != null)previous_marker.clear();

                            if (checkBoxRestaurant.isChecked()){
                                Log.d(TAG, "NRPlaces.Builder: start");
                                NRPlaces.Builder nrPlaces = new NRPlaces.Builder();
                                nrPlaces.listener(MapsActivity.this)
                                        .key("AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI")
                                        .latlng(destinationLatitude, destinationLongitude)
                                        .radius(500)
                                        .type(PlaceType.RESTAURANT)
                                        .language("ko", "KR")
                                        .build()
                                        .execute();
                                nrPlaces.listener(MapsActivity.this)
                                        .key("AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI")
                                        .latlng(destinationLatitude, destinationLongitude)
                                        .radius(500)
                                        .type(PlaceType.CAFE)
                                        .language("ko", "KR")
                                        .build()
                                        .execute();
//                                new NRPlaces.Builder()
//                                        .listener(MapsActivity.this)
//                                        .key("AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI")
//                                        .latlng(destinationLatitude, destinationLongitude)
//                                        .radius(500)
//                                        .type(PlaceType.RESTAURANT)
//                                        .language("ko", "KR")
//                                        .build()
//                                        .execute();
//                                new NRPlaces.Builder()
//                                        .listener(MapsActivity.this)
//                                        .key("AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI")
//                                        .latlng(destinationLatitude, destinationLongitude)
//                                        .radius(500)
//                                        .type(PlaceType.CAFE)
//                                        .language("ko", "KR")
//                                        .build()
//                                        .execute();
                                mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(destinationLatitude, destinationLongitude))
                                        .radius(500)
                                        .strokeColor(Color.parseColor("#884169e1"))
                                        .fillColor(Color.parseColor("#5587cefa")));
                            }

                            //친구
                            mMap.addMarker(options);
                            //나
                            mMap.addMarker(options2);
                            //목적지
                            mMap.addMarker(options3);



                        } else {
                            Log.d(TAG, "current location null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException");
        }
    }

    private void NRPlace(final LatLng latLng, final String placeType){
                    Log.d(TAG , "checkBoxRestaurantClick: "+ checkBoxRestaurant.isChecked());
                    new NRPlaces.Builder()
                            .listener(MapsActivity.this)
                            .key("AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI")
                            .latlng(latLng.latitude, latLng.longitude)
                            .radius(500)
                            .type(placeType)
                            .language("ko", "KR")
                            .build()
                            .execute();

        //목적지 마커 주변 범위 표시
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(latLng.latitude, latLng.longitude))
                .radius(500)
                .strokeColor(Color.parseColor("#884169e1"))
                .fillColor(Color.parseColor("#5587cefa")));
    }


    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera Lat: "+latLng.latitude+" Lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

//        mMap.clear();

        //커스텀 정보창을 생성합니다.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        if (placeInfo != null){
            try{
                //제목아래 표시되는 추가 텍스트를 설정
                String snippet = "Address: "+ placeInfo.getAddress() + "\n"+
                        "Phone number: "+ placeInfo.getPhoneNumber() + "\n"+
                        "Website: "+ placeInfo.getWebsiteUri() + "\n"+
                        "Price Rating: "+ placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
//                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_flag))
                        .draggable(true);

              mMarker = mMap.addMarker(options);
              NRPlace(latLng, PlaceType.RESTAURANT);
            }catch (NullPointerException e){
                Log.d(TAG, "movecamera: nullpointException");
            }
        }else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    //카메라 위치 변경 메서드
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera Lat: "+latLng.latitude+" Lng: "+latLng.longitude);
        //지도화면을 설정한 latLng(위도,경도값) 위치로 이동시킴
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")){
            //지도에 마커를 설정합니다.
            MarkerOptions options = new MarkerOptions()
                    //마커의 위치를 설정(필수)
                    .position(latLng)
                    //마커의 타이틀을 설정
                    .title(title);
                    /*
                    그외의 다른 MarkerOption들
                    Anchor : 마커의 LatLng 위치에 배치될 이미지의 지점. 기본값은 이미지 하단중앙
                    Alpha : 마커의 투명도 설정, 기본값은 1.0
                    Snippet : 제목 아래에 표시되는 추가 텍스트
                    Icon : 기본 마커 이미지 대신 표시되는 비트맵
                    Drag Status : 마커 드래그 가능여부 설정, 기본값은 false
                    Visibility : 마커 가시성 설정
                    Tag : 마커와 연결된 객체?!
                     */

            //마커를 추가한다.
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    //소프트 키보드드 숨기기 메서드
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    //지도 검색시 자동완성되는 검색어 클릭 이벤트, 선택한 장소의 정보를 가져온다.
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    //검색 한 장소의 이름,주소,핸드폰 번호,웹사이트 정보를 반환
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult"+places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: "+place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: "+place.getAddress());
//                mPlace.setAddtributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: addtributions: "+place.getAttributions());
                mPlace.setId(place.getId().toString());
                Log.d(TAG, "onResult: id: "+place.getId());
                mPlace.setLatLng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: "+place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: "+place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phonenumber: "+place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website: "+place.getWebsiteUri());
            }catch (NullPointerException e){
                Log.d(TAG, "onResult: NullPointException"+e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

//            places.release();
        }
    };

    //권한정보 가져오기
    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        //FINELOCATION 권한이 있는지 체크함, 권한이 있는경우 PackageManager.PERMISSION_GRANTED를 반환한다.
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //COURSE_LOCATION 권한이 있는지 체크함, 권한이 있는경우  PackageManager.PERMISSION_GRANTED를 반환한다.
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                //지도 실행 메서드
                initMap();
            } else {
                //권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //지도를 보여줍니다.
    private void initMap() {
        Log.d(TAG,"initMap");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //getMapAsync를 호출하여 googlemap객체가 준비될 때 실행될 콜백을 등록
        mapFragment.getMapAsync(MapsActivity.this);
    }

    //권한 요청시 응답처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "permissionRequest: called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //지도 실행 메서드
                    initMap();
                }
            }
        }
    }

//    private void createLocationCallback() {
//        mLocationCallBack = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                mCurrentLocation = locationResult.getLastLocation();
//
//                LatLng mMyLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyLatLng, 17));
//                updateLocationUI();
//            }
//        };
//    }
//
//    @SuppressWarnings("MissingPermission")
//    private void updateLocationUI() {
//        if (mMap == null) return;
//        if (mLocationPermissionGranted) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        } else {
//            mMap.setMyLocationEnabled(false);
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        }
//    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //맵이 사용될 준비가 되었을 때 호출되는 메서드
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            Log.d(TAG,"MapReady");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //활성화되면 지도 오른쪽 위에 My Location 버튼이 나타남, 클릭했을 때 현재위치를 아는경우 지도중앙에 표시해줌
            mMap.setMyLocationEnabled(true);
            //false인 경우 My Location 버튼이 보이지 않게함함
           mMap.getUiSettings().setMyLocationButtonEnabled(false);


            init();
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onPlaceseSuccess");
//                mClusterManager = new ClusterManager<>(MapsActivity.this,mMap);
//                renderer = new CustomClusterRenderer(MapsActivity.this, mMap, mClusterManager);
//                mClusterManager.setRenderer(renderer);
//                mMap.setOnCameraIdleListener(mClusterManager);
//                mMap.setOnMarkerClickListener(mClusterManager);
//                mMap.setOnInfoWindowClickListener(mClusterManager);
//                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

//                Geocoder geocoder = new Geocoder(MapsActivity.this);
                Log.d(TAG, "onPlaceseSuccess: checkBoxRestaurant: "+checkBoxRestaurant.isChecked());
                Log.d(TAG, String.valueOf(places.size()));
                for (final noman.googleplaces.Place place : places){
                    final String placeId = place.getPlaceId();
//                    LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
//                    String markerSnippet = getCurrentAddress(latLng);
//                    MarkerOptions options = new MarkerOptions()
//                            .position(latLng)
//                            .title(place.getName())
//                            .snippet(markerSnippet);
//                    Marker item = mMap.addMarker(options);
//                    previous_marker.add(item);

                    Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(@NonNull PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount()>0){
                                myPlace = places.get(0);
                                Log.d(TAG, "onPlaceseSuccess: myPlace: placeName: "+myPlace.getName());
                                Log.d(TAG, "onPlaceseSuccess: myPlace: placeType: "+ myPlace.getPlaceTypes().get(0));
                                mStringClusterItem = new StringClusterItem(myPlace.getName(), myPlace.getAddress(), myPlace.getLatLng(), myPlace.getPlaceTypes().get(0), myPlace.getPhoneNumber());
                                mClusterManager.addItem(mStringClusterItem);
                            }else if (places.getCount()==0){
                                Log.d(TAG, "onPlaceseSuccess: places.getCount:" +places.getCount());
//                                Toast.makeText(MapsActivity.this, "주변 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

//                    try {
//                        clusterAddresses = geocoder.getFromLocation(place.getLatitude(), place.getLongitude(),1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (clusterAddresses.size()>0){
//                        String stringAddress = clusterAddresses.get(0).getAddressLine(0).toString();
//                        mClusterManager.addItem(new StringClusterItem(place.getName(), stringAddress,new LatLng(place.getLatitude(), place.getLongitude())));
//                    }

                }
//                mClusterManager.cluster();

//                mGoogleApiClient.disconnect();

//                HashSet<Marker> hashSet = new HashSet<Marker>();
//                hashSet.addAll(previous_marker);
//                previous_marker.clear();
//                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Routes> routes) {
        Log.d(TAG, "onDirectionFinderSuccess");
        polylinePaths = new ArrayList<>();
//        for (Routes route : routes) {
////            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
////            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
////            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
////
////            originMarkers.add(mMap.addMarker(new MarkerOptions()
////                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
////                    .title(route.startAddress)
////                    .position(route.startLocation)));
////            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
////                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
////                    .title(route.endAddress)
////                    .position(route.endLocation)));
//
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.RED).
//                    width(10);
////            PolylineOptions test = new PolylineOptions()
////                    .geodesic(true)
////                    .color(Color.RED)
////                    .width(10);
////            test.add(currentLatLng);
////            test.add(new LatLng(37.48401694218727, 126.9724350162383));
////            test.add(new LatLng(37.485383625316,126.98222019800409));
////            test.add(new LatLng(37.48768633991912,126.99362465042393));
////            test.add(new LatLng(37.481459356778,126.99760224187544));
////            test.add(new LatLng(37.484975949628506,127.01626711046924));
////            test.add(new LatLng(37.493122181019885,127.01378377302518));
////            test.add(new LatLng(37.496813605110596,127.0241632794851));
////            test.add(new LatLng(37.49682192940073,127.02371332020543));
////            test.add(itemLatLng);
////            polylinePaths.add(mMap.addPolyline(test));
//            Log.d(TAG, "routesName: "+route.routesName);
//            for (int i = 0; i < route.points.size(); i++) {
////                if (i>0) {
////                    if (route.points.get(i) != route.points.get(i - 1)) {
//                        LatLng latLng = route.points.get(i);
//                        polylineOptions.add(latLng);
//                        Log.d(TAG, "onDirectionFinderSuccess: pointLatLng: " + latLng);
////                        Log.d(TAG, "onDirectionFinderSuccess: routesLatLng: " + routes.get(i).points.get(i));
////                    }
//            }
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
//        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(Color.RED)
                .width(10);

//        for (Routes route : routes) {
//            Log.d(TAG, "---------------------------------------------route : routes-------------------------------------------------");
//            for (int k=0; k<route.points.size(); k++){
//                coordinatesLatLng = route.points.get(k);
//                polylineOptions.add(coordinatesLatLng);
//                Log.d(TAG, "polyLineOption: addLatLng: "+coordinatesLatLng);
//            }
////                Log.d(TAG, "onDirectionFinderSuccess: pointLatLng: " + latLng);
//        }polylinePaths.add(mMap.addPolyline(polylineOptions));

        for (Routes route : routes){
            if (route.type.equals("init")){
                Log.d(TAG, "onDirectionFinderSuccess: totalTime: "+route.totalTime);
                Log.d(TAG, "onDirectionFinderSuccess: taxiFare: "+route.taxiFare);
                Log.d(TAG, "onDirectionFinderSuccess: totalDirection: "+route.totalDistance);

                double distance = route.totalDistance/1000;
                String stringDistance = String.format("%.1f", distance);
                int time = (int)route.totalTime/60;

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> adlist = new ArrayList<>();
                try{
                    //주소, 지명을 통해 좌표로 변환, 좌표를 주소나 지명으로 변환하는 경우에는 getFromLocation() 메서드를 사용
                    adlist = geocoder.getFromLocation(currentLatitude, currentLongitude,1);
//                    Log.d(TAG, "출발지 JSon: "+adlist.toString());
//                    Log.d(TAG, "출발지 정보 getAddressLine: "+adlist.get(0).getAddressLine(0));
//                    Log.d(TAG, "출발지 정보 getAdminArea: "+adlist.get(0).getAdminArea());
//                    Log.d(TAG, "출발지 정보 getFeatureName: "+adlist.get(0).getFeatureName());
//                    Log.d(TAG, "출발지 정보 getLocality: "+adlist.get(0).getLocality());
//                    Log.d(TAG, "출발지 정보 getSubAdminArea: "+adlist.get(0).getSubAdminArea());
//                    Log.d(TAG, "출발지 정보 getSubLocality: "+adlist.get(0).getSubLocality());
//                    Log.d(TAG, "출발지 정보 getThoroughfare: "+adlist.get(0).getThoroughfare());
//                    Log.d(TAG, "출발지 정보 getSubThoroughfare: "+adlist.get(0).getSubThoroughfare());

                    startPoint.setText("출발지 : "+adlist.get(0).getSubLocality()+" "+adlist.get(0).getThoroughfare()+", "+adlist.get(0).getSubThoroughfare());
                }catch (IOException e){
                    Log.d(TAG, "goeLocate IOException");
                }

                //분을 시간:분으로 분리하기
                long hour = TimeUnit.MINUTES.toHours(time);
                long minutes = TimeUnit.MINUTES.toMinutes(time) - TimeUnit.HOURS.toMinutes(hour);

                //도착시간 구하기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
                SimpleDateFormat sdfMin = new SimpleDateFormat("mm");
                int nowHour = Integer.parseInt(sdfHour.format(date));
                int nowMin = Integer.parseInt(sdfMin.format(date));

                if (time<60){
                    totalTime.setText("소요시간 : "+time+"분");
                    if ((nowMin+time)<60){
                        endTime.setText("도착시간 : "+nowHour+"시 "+(time+nowMin)+"분");
                    }else {
                        endTime.setText("도착시간 : "+(nowHour+1)+"시 "+ (time+nowMin-60)+"분");
                    }
                }else {
                    totalTime.setText("소요시간 : "+hour+"시간 "+minutes+"분");
                    if ((nowMin+time)<60){
                        endTime.setText("도착시간 : "+(nowHour+hour)+"시 "+(time+nowMin)+"분");
                    }else {
                        endTime.setText("도착시간 : "+(nowHour+hour+1)+"시 "+(time+nowMin-60)+"분");
                    }
                }
                endPoint.setText("목적지 : "+tvName.getText());
                totalDirection.setText("거리 : "+stringDistance+"Km");
                taxiFare.setText("택시요금 : "+route.taxiFare+"원");

            }else if (route.type.equals("run")){
                Log.d(TAG, "onDirectionFinderSuccess: routesName: "+route.routesName);
                if (route.description != null){
                    Log.d(TAG, "onDirectionFinderSuccess: description: "+route.description);
                    if (route.description.contains("우회전")){
                        findDirectionList.add(new FindDirectionItem(route.description,R.drawable.turnright));
                    }else if (route.description.contains("좌회전")){
                        findDirectionList.add(new FindDirectionItem(route.description,R.drawable.turnleft));
                    }else if (route.description.contains("U턴")){
                        findDirectionList.add(new FindDirectionItem(route.description,R.drawable.uturn));
                    }else if (route.description.contains("도착")) {
                        findDirectionList.add(new FindDirectionItem(route.description, R.drawable.arrival));
                    }else {
                        findDirectionList.add(new FindDirectionItem(route.description, R.drawable.gostraight));
                    }
                    findDirectionAdapter.notifyDataSetChanged();
                }
            }
        }

        for (int z=0; z<routes.get(1).points.size(); z++){
            coordinatesLatLng = routes.get(1).points.get(z);
            polylineOptions.add(coordinatesLatLng);
            Log.d(TAG, "onDirectionFinderSuccess: polyLineOption: addLatLng: "+coordinatesLatLng);
        }
        polylinePaths.add(mMap.addPolyline(polylineOptions));

//        for (int x=0; x<10; x++){
//            findDirectionList.add(new FindDirectionItem("테스트"+x));
//            findDirectionAdapter.notifyDataSetChanged();
//        }



        findDirection.setVisibility(View.VISIBLE);



    }

    @Override
    public void onTransitDirectionFinderStart() {

    }

    @Override
    public void onTransitDirectionFinderSuccess(List<TransitRoute> route) {

    }



    static class StringClusterItem implements ClusterItem{

        CharSequence title;
        LatLng latLng;
        CharSequence address;
        Integer placeType;
        CharSequence phonNumber;

        public StringClusterItem(CharSequence title, CharSequence address, LatLng latLng, Integer placeType, CharSequence phoneNumber) {
            this.title = title;
            this.latLng = latLng;
            this.address = address;
            this.placeType = placeType;
            this.phonNumber = phoneNumber;
        }

        public CharSequence getTitle() {
            return title;
        }

        public void setTitle(CharSequence title) {
            this.title = title;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

        public CharSequence getAddress() {
            return address;
        }

        public void setAddress(CharSequence address) {
            this.address = address;
        }

        public Integer getInteger() {
            return placeType;
        }

        public void setInteger(Integer integer) {
            this.placeType = integer;
        }

        public CharSequence getPhonNumber() {
            return phonNumber;
        }

        public void setPhonNumber(CharSequence phonNumber) {
            this.phonNumber = phonNumber;
        }

        @Override
        public LatLng getPosition() {
            return latLng;
        }
    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<MapsActivity.StringClusterItem>{
        private final Context mContext;

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<StringClusterItem> clusterManager) {
            super(context, map, clusterManager);
            mContext = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(final StringClusterItem item, final MarkerOptions markerOptions) {
            Log.d(TAG, "onBeforeClusterItemRendered");
            super.onBeforeClusterItemRendered(item, markerOptions);
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

//            mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<StringClusterItem>() {
//                @Override
//                public void onClusterItemInfoWindowClick(StringClusterItem stringClusterItem) {
//                    Log.d(TAG, "stringClusterItemClick: stringClusterItemInfo: title: "+stringClusterItem.title+" phoneNumber: "+stringClusterItem.phonNumber);
//                    infoWindow.setVisibility(View.VISIBLE);
//                    tvName.setText(stringClusterItem.title);
//                    tvAddress.setText(stringClusterItem.address);
//                    tvPhoneNumber.setText(stringClusterItem.phonNumber);
//                }
//            });



            //마커 클릭하면 정보창 띄움
            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<StringClusterItem>() {
                @Override
                public boolean onClusterItemClick(StringClusterItem stringClusterItem) {
                    Log.d(TAG, "stringClusterItemClick: stringClusterItemInfo: title: "+stringClusterItem.title+" phoneNumber: "+stringClusterItem.phonNumber+" placeType: "+stringClusterItem.placeType+" position: "+String.valueOf(stringClusterItem.getPosition()));

                    if (findDirection.getVisibility() == View.VISIBLE){
                        findDirection.setVisibility(View.INVISIBLE);
                    }

                    //마커 클릭하면 여기로와 버튼 활성화
                    if (comeHere.getVisibility() == View.INVISIBLE){
                        comeHere.setVisibility(View.VISIBLE);
                    }

                    infoWindow.setVisibility(View.VISIBLE);
                    tvName.setText(stringClusterItem.title);
                    tvAddress.setText(stringClusterItem.address);
                    tvPhoneNumber.setText(stringClusterItem.phonNumber);
                    itemLatitude.setText( String.valueOf(stringClusterItem.getPosition().latitude));
                    itemLongitude.setText(String.valueOf(stringClusterItem.getPosition().longitude));
                    return false;




                }
            });

//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    Log.d(TAG, "onMarkerClick: itemInfo: itemTitle: "+item.title+" itemPhoneNumber: "+item.phonNumber);
//                    infoWindow.setVisibility(View.VISIBLE);
//                    tvName.setText(item.title);
//                    tvAddress.setText(item.address);
//                    tvPhoneNumber.setText(item.phonNumber);
//                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                    return false;
//                }
//            });


            //placeType이 79(식당)인경우랑 그렇지 않은경우
            if (checkBoxRestaurant.isChecked()){
                Log.d(TAG, "checkBoxRestaurant: isChecked: "+checkBoxRestaurant.isChecked());
               if (item.placeType ==79){
                   markerOptions
                           .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                           .title((String) item.title)
                           .visible(true);
                   Log.d(TAG, "restaurantMarkerOption: visible: true");
               }
            }else {
                Log.d(TAG, "checkBoxRestaurant: isChecked: "+checkBoxRestaurant.isChecked());
                if (item.placeType==79){
                    markerOptions
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                            .title((String) item.title)
                            .visible(false);
                    Log.d(TAG, "restaurantMarkerOption: visible: false");
                }
            }

                    if (checkBoxCafe.isChecked()){
                        Log.d(TAG, "checkBoxCafe: isChecked: "+checkBoxCafe.isChecked());
                        if (item.placeType != 79){
                            markerOptions
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                    .title((String)item.title)
                                    .visible(true);
                            Log.d(TAG, "cafeMarkerOption: visible: true");
                        }
                    }else {
                        Log.d(TAG, "checkBoxCafe: isChecked: "+checkBoxCafe.isChecked());
                        if (item.placeType != 79){

                            markerOptions
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                    .title((String)item.title).
                                    visible(false);
                            Log.d(TAG, "cafeMarkerOption: visible: false");
                        }
                    }

            //placeType이 79(식당)인 경우에는 하늘색 마커, 그외(카페)의 경우에는 노란색마커로 표시
//            if (item.placeType==79){
//                markerOptions
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
//                        .title((String)item.title);
//            }else {
//                markerOptions
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
//                        .title((String)item.title);
//            }

//            String snippet = "주소: "+ item.address + "\n"+
//                    "전화: "+ item.phonNumber + "\n";
//            Log.d(TAG, "onBeforeClusterItemRendered: itemCheckBoxRestaurant: "+checkBoxRestaurant.isChecked());
//            if (checkBoxRestaurant.isChecked()){
//                markerOptions
//                        .icon(markerDescrptor)
//                        .title((String) item.title);
////                        .snippet(snippet)
//            }else {
//                Log.d(TAG, "onBeforeClusterItemRendered: markerOption: visible(false)");
//                markerOptions
//                        .icon(markerDescrptor)
//                        .title((String) item.title)
////                        .snippet((String) item.address)
//                        .visible(false);
//                mClusterManager.clearItems();
//            }

//            if (checkBoxCafe.isChecked()){
//                markerOptions
//                        .icon(markerDescrptor)
//                        .title((String) item.title)
//                        .snippet((String) item.address);
//            }else {
//                markerOptions
//                        .icon(markerDescrptor)
//                        .title((String) item.title)
//                        .snippet((String) item.address)
//                        .visible(false);
//                mClusterManager.clearItems();
//            }
        }
    }

    public String getCurrentAddress(LatLng latLng) {
        Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e(TAG,"서비스 사용 불가");
        } catch (IllegalArgumentException ex) {
            Log.e(TAG,"잘못된 GPS 좌표");
        }
        if (addresses == null || addresses.size()==0){
            return "주소없음";
        }else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    @Override
    public void onPlacesFinished() {

    }
}
