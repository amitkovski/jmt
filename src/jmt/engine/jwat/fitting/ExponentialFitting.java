package jmt.engine.jwat.fitting;

import java.util.Arrays;

public class ExponentialFitting implements FittingAlgorithm {
	private static final double [][] crit_values = 
	   {{0.25892,0.99811},
		{0.14332,0.93346},
		{0.10654,0.78408},
		{0.08342,0.58255},
		{0.06621,0.49198},
		{0.06114,0.39642},
		{0.05411,0.35274},
		{0.04904,0.30659},
		{0.04444,0.26267},
		{0.0421,0.22522},
		{0.0383,0.20073},
		{0.03555,0.18569},
		{0.03326,0.16308},
		{0.03141,0.15073},
		{0.03033,0.1393},
		{0.02986,0.13279},
		{0.02711,0.12032},
		{0.02628,0.113},
		{0.02488,0.10484},
		{0.02424,0.09944},
		{0.02385,0.09386},
		{0.02269,0.08733},
		{0.02185,0.08227},
		{0.02129,0.07734},
		{0.02099,0.07381},
		{0.02029,0.07271},
		{0.01976,0.06888},
		{0.01882,0.06676},
		{0.01854,0.06276},
		{0.01823,0.06062},
		{0.01739,0.05795},
		{0.01734,0.05513},
		{0.01634,0.05369},
		{0.01624,0.05144},
		{0.01592,0.04992},
		{0.01573,0.04795},
		{0.01544,0.04545},
		{0.01527,0.04527},
		{0.01473,0.04317},
		{0.01436,0.0426},
		{0.01428,0.04113},
		{0.01374,0.04019},
		{0.01362,0.03888},
		{0.0133,0.03821},
		{0.01282,0.03637},
		{0.01262,0.03614},
		{0.01262,0.03524},
		{0.01243,0.03396},
		{0.01228,0.0334},
		{0.0119,0.03196},
		{0.01165,0.03181},
		{0.01198,0.03104},
		{0.01115,0.03017},
		{0.01138,0.02951},
		{0.0111,0.0289},
		{0.011,0.02819},
		{0.01078,0.02716},
		{0.01045,0.02671},
		{0.01044,0.02671},
		{0.01049,0.02638},
		{0.01041,0.02533},
		{0.0103,0.02503},
		{0.00996,0.02446},
		{0.01005,0.02377},
		{0.00982,0.02332},
		{0.00986,0.02356},
		{0.00962,0.02256},
		{0.00929,0.02279},
		{0.00914,0.02212},
		{0.00896,0.02182},
		{0.00914,0.02134},
		{0.00903,0.02077},
		{0.0090,0.02066},
		{0.00874,0.02051},
		{0.00878,0.02005},
		{0.00878,0.02024},
		{0.00842,0.01943},
		{0.00849,0.01903},
		{0.00821,0.0187},
		{0.00815,0.01853},
		{0.00833,0.01802},
		{0.00814,0.01791},
		{0.00781,0.0179},
		{0.00799,0.0174},
		{0.00798,0.01736},
		{0.00778,0.01695},
		{0.00777,0.01674},
		{0.00751,0.01659},
		{0.00752,0.01619},
		{0.00739,0.01602},
		{0.00755,0.01577},
		{0.00731,0.01593},
		{0.00718,0.01555},
		{0.00706,0.01523},
		{0.00712,0.01495},
		{0.00687,0.01515},
		{0.00694,0.01454},
		{0.0069,0.01454},
		{0.00692,0.01455},
		{0.00691,0.01455},
		{0.00681,0.01407},
		{0.00661,0.01401},
		{0.00692,0.01389},
		{0.00655,0.01351},
		{0.00665,0.0135},
		{0.00664,0.01321},
		{0.00644,0.01319},
		{0.00625,0.01308},
		{0.0064,0.01301},
		{0.00634,0.01289},
		{0.00642,0.01281},
		{0.00615,0.01261},
		{0.00626,0.01219},
		{0.00612,0.0123},
		{0.00606,0.01217},
		{0.00615,0.01198},
		{0.0062,0.01204},
		{0.00599,0.01187},
		{0.00594,0.0116},
		{0.00594,0.01156},
		{0.00579,0.01168},
		{0.00578,0.01137},
		{0.00578,0.01117},
		{0.00579,0.01119},
		{0.00578,0.01108},
		{0.00569,0.01097},
		{0.00565,0.01092},
		{0.00559,0.0108},
		{0.00553,0.01057},
		{0.00551,0.01045},
		{0.00539,0.01041},
		{0.00529,0.01036},
		{0.0054,0.01014},
		{0.00533,0.01023},
		{0.00529,0.01005},
		{0.00536,0.01007},
		{0.00524,0.00978},
		{0.00525,0.00975},
		{0.00526,0.00961},
		{0.00517,0.00961},
		{0.00507,0.00956},
		{0.00517,0.00976},
		{0.00498,0.0095},
		{0.00501,0.00943},
		{0.00498,0.00935},
		{0.00502,0.00909},
		{0.00484,0.00911},
		{0.00492,0.00894},
		{0.00491,0.00906},
		{0.00484,0.00897},
		{0.00481,0.0089},
		{0.00481,0.00896},
		{0.00478,0.00877},
		{0.00475,0.00853},
		{0.00466,0.00859},
		{0.00468,0.00863},
		{0.00475,0.00844},
		{0.00453,0.00857},
		{0.00464,0.00847},
		{0.0045,0.0083},
		{0.00456,0.00809},
		{0.00457,0.00814},
		{0.00458,0.00818},
		{0.00455,0.00808},
		{0.00441,0.00792},
		{0.00448,0.0080},
		{0.00431,0.00798},
		{0.00448,0.00792},
		{0.0044,0.00794},
		{0.00439,0.00782},
		{0.0043,0.0077},
		{0.00426,0.00772},
		{0.00417,0.00764},
		{0.0043,0.0076},
		{0.00425,0.00748},
		{0.00421,0.00744},
		{0.00427,0.0074},
		{0.00415,0.00726},
		{0.00414,0.00744},
		{0.00424,0.00731},
		{0.00415,0.0073},
		{0.00413,0.0072},
		{0.00413,0.00712},
		{0.00413,0.00709},
		{0.00418,0.00713},
		{0.00399,0.00701},
		{0.00398,0.00689},
		{0.00394,0.00689},
		{0.00395,0.00697},
		{0.00396,0.00689},
		{0.00399,0.00685},
		{0.00394,0.00681},
		{0.00394,0.00679},
		{0.00388,0.00681},
		{0.00398,0.00669},
		{0.00381,0.00661},
		{0.00382,0.0066},
		{0.00387,0.0067},
		{0.00385,0.00644},
		{0.00379,0.00649},
		{0.0037,0.00641},
		{0.00369,0.00638},
		{0.00365,0.00638},
		{0.00372,0.00637},
		{0.00368,0.0063},
		{0.00371,0.00632},
		{0.00363,0.00621},
		{0.00365,0.00626},
		{0.00367,0.00613},
		{0.00364,0.00608},
		{0.00367,0.00601},
		{0.00357,0.00614},
		{0.00366,0.00602},
		{0.00359,0.0061},
		{0.00359,0.00587},
		{0.00344,0.00592},
		{0.00357,0.00595},
		{0.00352,0.00603},
		{0.00352,0.00583},
		{0.00355,0.00584},
		{0.00344,0.00572},
		{0.00345,0.00577},
		{0.00346,0.00577},
		{0.00334,0.0058},
		{0.00347,0.00568},
		{0.00341,0.0057},
		{0.00336,0.00572},
		{0.00336,0.00559},
		{0.00332,0.00564},
		{0.00335,0.00551},
		{0.00336,0.00552},
		{0.00337,0.00555},
		{0.00327,0.00543},
		{0.00332,0.00544},
		{0.00327,0.00547},
		{0.00331,0.00538},
		{0.00329,0.00531},
		{0.00325,0.00537},
		{0.00326,0.00527},
		{0.0033,0.00531},
		{0.00324,0.00528},
		{0.00319,0.00516},
		{0.0032,0.00514},
		{0.00318,0.00521},
		{0.00319,0.00517},
		{0.00312,0.0051},
		{0.00317,0.00512},
		{0.0031,0.00513},
		{0.00318,0.00506},
		{0.00318,0.00514},
		{0.00318,0.00504},
		{0.00307,0.00508},
		{0.0031,0.00506},
		{0.0031,0.00497},
		{0.00305,0.00493},
		{0.00298,0.00493},
		{0.00311,0.00491},
		{0.00309,0.00492},
		{0.00305,0.00488},
		{0.00304,0.00478},
		{0.00292,0.00475},
		{0.0030,0.00482},
		{0.00303,0.00474},
		{0.00299,0.0048},
		{0.00303,0.00473},
		{0.00295,0.00478},
		{0.00289,0.00466},
		{0.00295,0.0047},
		{0.0030,0.00467},
		{0.00295,0.00465},
		{0.00288,0.00461},
		{0.00286,0.0046},
		{0.00284,0.00462},
		{0.00286,0.00464},
		{0.00276,0.00456},
		{0.00282,0.00451},
		{0.00289,0.00453},
		{0.00275,0.00452},
		{0.00288,0.00448},
		{0.00282,0.00441},
		{0.00285,0.00442},
		{0.00276,0.00446},
		{0.0028,0.00439},
		{0.00277,0.00442},
		{0.00278,0.0043},
		{0.00281,0.00435},
		{0.00274,0.00431},
		{0.00283,0.0043},
		{0.00278,0.00426},
		{0.00275,0.00434},
		{0.00271,0.00429},
		{0.00271,0.00427},
		{0.00266,0.00424},
		{0.00265,0.00427},
		{0.00268,0.00428},
		{0.00264,0.00428},
		{0.00267,0.00414},
		{0.00265,0.00421},
		{0.00264,0.00414},
		{0.00269,0.00413},
		{0.00265,0.00417},
		{0.00262,0.00405},
		{0.00261,0.00411},
		{0.00266,0.00406},
		{0.00261,0.00406},
		{0.00263,0.0040},
		{0.00262,0.00403},
		{0.00255,0.00399},
		{0.00257,0.00406},
		{0.00257,0.00399},
		{0.00253,0.00395},
		{0.0025,0.00396},
		{0.00261,0.00391},
		{0.00253,0.00393},
		{0.00255,0.00394},
		{0.00251,0.00392},
		{0.0025,0.0039},
		{0.00253,0.00395},
		{0.00255,0.00392},
		{0.00251,0.00389},
		{0.00248,0.00381},
		{0.00247,0.00382},
		{0.00247,0.00374},
		{0.00247,0.00386},
		{0.00247,0.00378},
		{0.00245,0.00374},
		{0.0025,0.00377},
		{0.00244,0.00377},
		{0.00247,0.00372},
		{0.00243,0.00371},
		{0.00242,0.00374},
		{0.00238,0.00375},
		{0.00237,0.0037},
		{0.0024,0.00368},
		{0.00234,0.00363},
		{0.00241,0.00367},
		{0.00238,0.00362},
		{0.00239,0.00362},
		{0.00235,0.00358},
		{0.00238,0.00361},
		{0.00234,0.00361},
		{0.0024,0.00361},
		{0.00238,0.00355},
		{0.00239,0.00356},
		{0.00237,0.00351},
		{0.00229,0.00356},
		{0.00233,0.00349},
		{0.00238,0.00348},
		{0.00228,0.00356},
		{0.00231,0.00356},
		{0.0023,0.0035},
		{0.0023,0.00349},
		{0.00235,0.00346},
		{0.00233,0.00339},
		{0.00229,0.00345},
		{0.00227,0.00342},
		{0.00227,0.0034},
		{0.00221,0.00338},
		{0.0023,0.00336},
		{0.00223,0.00335},
		{0.00221,0.00337},
		{0.00224,0.0034},
		{0.00223,0.00337},
		{0.00224,0.00334},
		{0.00222,0.00335},
		{0.00222,0.00332},
		{0.00215,0.00329},
		{0.00215,0.00334},
		{0.00221,0.0033},
		{0.00221,0.00331},
		{0.00217,0.00328},
		{0.00222,0.00326},
		{0.00217,0.00324},
		{0.00218,0.00324},
		{0.00217,0.00324},
		{0.00217,0.00323},
		{0.00211,0.00319},
		{0.00218,0.00323},
		{0.00216,0.00322},
		{0.00219,0.00321},
		{0.00211,0.00316},
		{0.00217,0.00314},
		{0.00214,0.00316},
		{0.00212,0.00313},
		{0.00212,0.00313},
		{0.00214,0.00312},
		{0.00206,0.00306},
		{0.00206,0.00308},
		{0.00209,0.00309},
		{0.00212,0.00311},
		{0.00209,0.00311},
		{0.00207,0.00307},
		{0.00205,0.00307},
		{0.00208,0.00306},
		{0.00206,0.00306},
		{0.00206,0.00309},
		{0.00208,0.0030},
		{0.00208,0.00303},
		{0.00205,0.00307},
		{0.00206,0.00301},
		{0.00201,0.00298},
		{0.00198,0.00295},
		{0.00203,0.00302},
		{0.00203,0.00297},
		{0.00201,0.00297},
		{0.00201,0.00299},
		{0.00203,0.0030},
		{0.0020,0.00298},
		{0.0020,0.00298},
		{0.00199,0.00291},
		{0.00197,0.00293},
		{0.00199,0.00291},
		{0.00199,0.00289},
		{0.00198,0.00291},
		{0.00201,0.00289},
		{0.00198,0.00287},
		{0.00199,0.0029},
		{0.0020,0.00288},
		{0.00193,0.00288},
		{0.00195,0.00288},
		{0.00197,0.00287},
		{0.00194,0.00286},
		{0.00194,0.00284},
		{0.00193,0.00286},
		{0.00194,0.00285},
		{0.00195,0.0028},
		{0.00195,0.0028},
		{0.00192,0.00277},
		{0.00192,0.00281},
		{0.00188,0.00282},
		{0.0019,0.00275},
		{0.00188,0.00278},
		{0.00192,0.00281},
		{0.0019,0.00272},
		{0.00189,0.00281},
		{0.00187,0.00276},
		{0.00187,0.00279},
		{0.00185,0.0027},
		{0.00189,0.00269},
		{0.00184,0.00273},
		{0.0019,0.00273},
		{0.00189,0.00272},
		{0.00188,0.00268},
		{0.00185,0.00271},
		{0.00188,0.00271},
		{0.00187,0.00269},
		{0.00186,0.00263},
		{0.00188,0.00267},
		{0.00182,0.00268},
		{0.00184,0.00265},
		{0.00186,0.00268},
		{0.00186,0.00262},
		{0.00184,0.00268},
		{0.0018,0.00265},
		{0.00184,0.00264},
		{0.00182,0.00263},
		{0.00182,0.0026},
		{0.00179,0.00263},
		{0.00181,0.00259},
		{0.00181,0.00258},
		{0.00179,0.00259},
		{0.0018,0.00256},
		{0.00181,0.00256},
		{0.00176,0.0026},
		{0.00178,0.00258},
		{0.00177,0.00256},
		{0.00181,0.00253},
		{0.00177,0.00252},
		{0.00179,0.00253},
		{0.00178,0.00255},
		{0.0018,0.00253},
		{0.00176,0.00251},
		{0.00179,0.00253},
		{0.00174,0.00252},
		{0.00174,0.0025},
		{0.00175,0.00251},
		{0.00171,0.00249},
		{0.00175,0.00249},
		{0.00172,0.00248},
		{0.00173,0.00249},
		{0.00175,0.00244},
		{0.00173,0.00245},
		{0.00174,0.00243},
		{0.00178,0.00243},
		{0.00172,0.00242},
		{0.00167,0.00243},
		{0.00173,0.00246},
		{0.00173,0.0024},
		{0.00171,0.00246},
		{0.00168,0.00244},
		{0.00169,0.00245},
		{0.0017,0.00239},
		{0.00172,0.00241},
		{0.00167,0.00243},
		{0.00169,0.00238},
		{0.00171,0.00239},
		{0.00167,0.00238},
		{0.00166,0.00236},
		{0.00167,0.00238},
		{0.00167,0.00236},
		{0.00166,0.00239},
		{0.00169,0.00234},
		{0.00161,0.00237},
		{0.00165,0.00233},
		{0.00167,0.00233},
		{0.00167,0.00237},
		{0.00167,0.00233},
		{0.00163,0.00235},
		{0.00165,0.00233},
		{0.00164,0.00231},
		{0.00167,0.00228},
		{0.00163,0.00229},
		{0.00164,0.00228},
		{0.00164,0.00233},
		{0.00164,0.00228},
		{0.00164,0.00225},
		{0.00163,0.00227},
		{0.00165,0.0023},
		{0.00161,0.00231},
		{0.00159,0.00225},
		{0.00162,0.00225},
		{0.00162,0.00227},
		{0.00162,0.00226},
		{0.00161,0.00227},
		{0.00162,0.00224},
		{0.00161,0.00226},
		{0.00159,0.00222},
		{0.00161,0.00224},
		{0.0016,0.00226},
		{0.0016,0.00226},
		{0.00159,0.00222},
		{0.0016,0.00221},
		{0.00158,0.00221},
		{0.00161,0.00224},
		{0.00157,0.0022},
		{0.00158,0.00224},
		{0.00158,0.00216},
		{0.00159,0.00222},
		{0.00155,0.00217},
		{0.00156,0.00221},
		{0.00154,0.00219},
		{0.00156,0.00214},
		{0.00156,0.00214},
		{0.00156,0.00221},
		{0.00154,0.00215},
		{0.00155,0.00215},
		{0.00154,0.00212},
		{0.00155,0.00219},
		{0.00149,0.00215},
		{0.00154,0.00217},
		{0.00154,0.00214},
		{0.00153,0.00212},
		{0.00153,0.00213},
		{0.00152,0.00212},
		{0.00154,0.00214},
		{0.00153,0.00215},
		{0.00152,0.00211},
		{0.00152,0.00211},
		{0.00151,0.00211},
		{0.0015,0.0021},
		{0.0015,0.00208},
		{0.00151,0.00207},
		{0.00152,0.00209},
		{0.0015,0.00205},
		{0.00149,0.00207},
		{0.00147,0.00206},
		{0.00148,0.00207},
		{0.00149,0.00207},
		{0.0015,0.00204},
		{0.00148,0.00206},
		{0.00145,0.00205},
		{0.00149,0.00204},
		{0.0015,0.00202},
		{0.00148,0.00202},
		{0.00144,0.00204},
		{0.00145,0.00204},
		{0.0015,0.00204},
		{0.00147,0.00202},
		{0.00145,0.0020},
		{0.00146,0.00201},
		{0.00147,0.00202},
		{0.00146,0.00201},
		{0.00145,0.0020},
		{0.00144,0.00203},
		{0.00142,0.00199},
		{0.00145,0.00198},
		{0.00143,0.00201},
		{0.00146,0.00201},
		{0.00144,0.00198},
		{0.00143,0.00197},
		{0.00145,0.00194},
		{0.00143,0.00195},
		{0.00141,0.00198},
		{0.00145,0.00194},
		{0.00144,0.00198},
		{0.00141,0.00199},
		{0.0014,0.00193},
		{0.00142,0.00196},
		{0.00144,0.00196},
		{0.00143,0.00195},
		{0.00139,0.00193},
		{0.0014,0.00191},
		{0.00139,0.00195},
		{0.00144,0.00191},
		{0.00141,0.00192},
		{0.0014,0.00193},
		{0.00141,0.00194},
		{0.00144,0.00193},
		{0.00138,0.0019},
		{0.00138,0.00191},
		{0.00141,0.00193},
		{0.00138,0.00193},
		{0.00139,0.0019},
		{0.00139,0.0019},
		{0.00137,0.0019},
		{0.00141,0.00189},
		{0.00138,0.00191},
		{0.00138,0.00191},
		{0.00138,0.00186},
		{0.00135,0.00187},
		{0.00137,0.00184},
		{0.00137,0.00185},
		{0.00136,0.00188},
		{0.00137,0.00188},
		{0.00138,0.00184},
		{0.00138,0.00188},
		{0.00135,0.00186},
		{0.00135,0.0019},
		{0.00135,0.00187},
		{0.00138,0.00188},
		{0.00135,0.00186},
		{0.00136,0.00184},
		{0.00136,0.00185},
		{0.00134,0.00183},
		{0.00134,0.00181},
		{0.00133,0.00183},
		{0.00134,0.00184},
		{0.00132,0.00182},
		{0.00134,0.00182},
		{0.00134,0.00182},
		{0.00136,0.00185},
		{0.00134,0.0018},
		{0.00132,0.00182},
		{0.00134,0.00178},
		{0.00132,0.0018},
		{0.00128,0.00183},
		{0.00133,0.00178},
		{0.00135,0.00179},
		{0.00132,0.00179},
		{0.00131,0.0018},
		{0.00133,0.0018},
		{0.0013,0.00177},
		{0.00129,0.0018},
		{0.00129,0.00176},
		{0.00133,0.00176},
		{0.00133,0.00176},
		{0.00132,0.0018},
		{0.00133,0.00179},
		{0.0013,0.00177},
		{0.00132,0.00176},
		{0.00131,0.00177},
		{0.00131,0.00172},
		{0.00128,0.00175},
		{0.0013,0.00173},
		{0.0013,0.00175},
		{0.00127,0.00178},
		{0.00129,0.00174},
		{0.0013,0.00173},
		{0.00126,0.00175},
		{0.00129,0.00173},
		{0.00129,0.00173},
		{0.00124,0.00173},
		{0.00124,0.0017},
		{0.00126,0.00173},
		{0.00129,0.0017},
		{0.00127,0.00174},
		{0.00125,0.00171},
		{0.00125,0.00173},
		{0.00128,0.0017},
		{0.00123,0.00169},
		{0.00124,0.00171},
		{0.00127,0.00169},
		{0.00125,0.00171},
		{0.00124,0.00173},
		{0.00125,0.00168},
		{0.00125,0.00171},
		{0.00126,0.00168},
		{0.00122,0.00168},
		{0.00125,0.00168},
		{0.00123,0.00167},
		{0.00124,0.00164},
		{0.00126,0.0017},
		{0.00124,0.00166},
		{0.00126,0.00167},
		{0.00122,0.00167},
		{0.00124,0.00166},
		{0.00122,0.00163},
		{0.00124,0.00165},
		{0.00123,0.00167},
		{0.00125,0.00165},
		{0.00121,0.00165},
		{0.00121,0.00164},
		{0.00122,0.00166},
		{0.00121,0.00163},
		{0.00122,0.00167},
		{0.00122,0.00164},
		{0.00121,0.00163},
		{0.00122,0.00163},
		{0.00121,0.00163},
		{0.00119,0.00164},
		{0.0012,0.00165},
		{0.00123,0.00163},
		{0.0012,0.00162},
		{0.0012,0.00162},
		{0.0012,0.00161},
		{0.00121,0.0016},
		{0.00119,0.00161},
		{0.00121,0.00162},
		{0.00119,0.00159},
		{0.00118,0.00162},
		{0.0012,0.0016},
		{0.00118,0.00159},
		{0.00116,0.00163},
		{0.0012,0.00159},
		{0.00122,0.0016},
		{0.00119,0.00158},
		{0.00118,0.00161},
		{0.00118,0.00159},
		{0.00119,0.0016},
		{0.00118,0.00157},
		{0.00118,0.00157},
		{0.00119,0.00159},
		{0.00117,0.00156},
		{0.00116,0.00155},
		{0.00118,0.00157},
		{0.00118,0.00157},
		{0.00115,0.00157},
		{0.0012,0.00154},
		{0.00114,0.00158},
		{0.00114,0.00156},
		{0.00116,0.00158},
		{0.00118,0.00155},
		{0.00117,0.00156},
		{0.00116,0.00156},
		{0.00114,0.00157},
		{0.00117,0.00153},
		{0.00116,0.00155},
		{0.00115,0.00153},
		{0.00115,0.00152},
		{0.00115,0.00154},
		{0.00116,0.00154},
		{0.00114,0.00152},
		{0.00114,0.00152},
		{0.00114,0.00151},
		{0.00116,0.00154},
		{0.00113,0.00159},
		{0.00116,0.0015},
		{0.00114,0.0015},
		{0.00113,0.00154},
		{0.00114,0.0015},
		{0.00112,0.00151},
		{0.00115,0.00149},
		{0.00115,0.00149},
		{0.00113,0.0015},
		{0.00116,0.00149},
		{0.00114,0.0015},
		{0.00114,0.00149},
		{0.00112,0.00149},
		{0.00113,0.00151},
		{0.00109,0.00147},
		{0.00111,0.00148},
		{0.00111,0.0015},
		{0.00112,0.00148},
		{0.0011,0.00148},
		{0.00113,0.00148},
		{0.00114,0.00148},
		{0.00107,0.00148},
		{0.0011,0.00147},
		{0.00111,0.00147},
		{0.00112,0.00148},
		{0.00113,0.00145},
		{0.0011,0.00147},
		{0.0011,0.00148},
		{0.00111,0.00145},
		{0.0011,0.00147},
		{0.0011,0.00146},
		{0.0011,0.00146},
		{0.00109,0.00144},
		{0.0011,0.00146},
		{0.00109,0.00146},
		{0.0011,0.00146},
		{0.00108,0.00144},
		{0.00108,0.00145},
		{0.0011,0.00145},
		{0.00106,0.00143},
		{0.00108,0.00145},
		{0.00111,0.00144},
		{0.0011,0.00142},
		{0.00108,0.00146},
		{0.00109,0.00146},
		{0.00108,0.00143},
		{0.00108,0.00144},
		{0.00105,0.00141},
		{0.00108,0.00144},
		{0.00108,0.00141},
		{0.00108,0.00141},
		{0.00106,0.00142},
		{0.00108,0.00142},
		{0.00106,0.00139},
		{0.00106,0.00141},
		{0.00107,0.00143},
		{0.00108,0.00141},
		{0.00108,0.0014},
		{0.00107,0.0014},
		{0.00105,0.00142},
		{0.00107,0.00142},
		{0.00108,0.0014},
		{0.00108,0.00141},
		{0.00106,0.0014},
		{0.00105,0.00138},
		{0.00106,0.0014},
		{0.00105,0.0014},
		{0.00107,0.00139},
		{0.00104,0.00138},
		{0.00104,0.00138},
		{0.00106,0.00137},
		{0.00106,0.00139},
		{0.00105,0.00141},
		{0.00104,0.00138},
		{0.00102,0.0014},
		{0.00104,0.00136},
		{0.00104,0.00136},
		{0.00105,0.00139},
		{0.00106,0.00135},
		{0.00105,0.00136},
		{0.00105,0.0014},
		{0.00103,0.00137},
		{0.00103,0.00137},
		{0.00103,0.00135},
		{0.00102,0.00138},
		{0.00101,0.00138},
		{0.00104,0.00137},
		{0.0010,0.00136},
		{0.0010,0.00135},
		{0.00104,0.00137},
		{0.00102,0.00134},
		{0.00102,0.00136},
		{0.00103,0.00134},
		{9.9E-4,0.00135},
		{0.0010,0.00134},
		{0.00103,0.00134},
		{0.00105,0.00134},
		{0.00102,0.00135},
		{0.00101,0.00134},
		{0.00101,0.00133},
		{0.00102,0.00134},
		{0.00102,0.00135},
		{0.00102,0.00133},
		{0.00102,0.00135},
		{0.00103,0.00133},
		{0.0010,0.00133},
		{0.00102,0.00133},
		{0.00102,0.00133},
		{0.0010,0.00133},
		{0.00104,0.00133},
		{0.00103,0.00134},
		{0.00101,0.00132},
		{0.00101,0.00131},
		{0.00101,0.00131},
		{0.00101,0.00129},
		{0.00101,0.0013},
		{9.8E-4,0.00128},
		{0.0010,0.00133},
		{0.00101,0.00131},
		{0.00101,0.00132},
		{9.8E-4,0.0013},
		{9.9E-4,0.0013},
		{9.7E-4,0.00129},
		{9.9E-4,0.0013},
		{0.0010,0.00129},
		{9.8E-4,0.00131},
		{9.8E-4,0.00128},
		{9.8E-4,0.00129},
		{9.9E-4,0.00129},
		{0.0010,0.0013},
		{9.8E-4,0.0013},
		{9.9E-4,0.00129},
		{0.0010,0.00128},
		{0.0010,0.00128},
		{9.9E-4,0.00127},
		{9.9E-4,0.00126},
		{0.00101,0.00127},
		{9.6E-4,0.00131},
		{0.0010,0.00127},
		{9.8E-4,0.00129},
		{9.7E-4,0.00127},
		{9.7E-4,0.00127},
		{9.7E-4,0.00126},
		{9.9E-4,0.00126},
		{9.7E-4,0.00128},
		{9.8E-4,0.00126},
		{9.8E-4,0.00126},
		{9.7E-4,0.00126},
		{9.6E-4,0.00126},
		{9.5E-4,0.00128},
		{9.6E-4,0.00125},
		{9.5E-4,0.00126},
		{9.8E-4,0.00125},
		{9.7E-4,0.00126},
		{9.6E-4,0.00127},
		{9.6E-4,0.00124},
		{9.6E-4,0.00126},
		{9.5E-4,0.00125},
		{9.6E-4,0.00124},
		{9.5E-4,0.00123},
		{9.5E-4,0.00124},
		{9.3E-4,0.00123},
		{9.6E-4,0.00123},
		{9.7E-4,0.00125},
		{9.5E-4,0.00125},
		{9.6E-4,0.00124},
		{9.7E-4,0.00123},
		{9.5E-4,0.00125},
		{9.7E-4,0.00122},
		{9.6E-4,0.00122},
		{9.5E-4,0.00122},
		{9.5E-4,0.00121},
		{9.5E-4,0.00121},
		{9.3E-4,0.00121},
		{9.4E-4,0.00121},
		{9.5E-4,0.00122},
		{9.5E-4,0.00122},
		{9.3E-4,0.00123},
		{9.4E-4,0.00122},
		{9.3E-4,0.00123},
		{9.3E-4,0.00119},
		{9.4E-4,0.0012},
		{9.3E-4,0.00121},
		{9.3E-4,0.00119},
		{9.0E-4,0.0012},
		{9.2E-4,0.0012},
		{9.3E-4,0.00121},
		{9.4E-4,0.00119},
		{9.4E-4,0.0012},
		{9.0E-4,0.0012},
		{9.3E-4,0.00121},
		{9.1E-4,0.00121},
		{9.2E-4,0.00121},
		{9.2E-4,0.0012},
		{9.2E-4,0.00119},
		{9.3E-4,0.00119},
		{9.1E-4,0.00118},
		{9.2E-4,0.00121},
		{9.2E-4,0.0012},
		{9.1E-4,0.00119},
		{9.2E-4,0.00116},
		{9.0E-4,0.00118},
		{9.0E-4,0.0012},
		{9.0E-4,0.00117},
		{9.2E-4,0.00118},
		{9.1E-4,0.00118},
		{9.3E-4,0.00117},
		{9.0E-4,0.00115},
		{9.1E-4,0.00116},
		{9.1E-4,0.00119},
		{9.1E-4,0.00117},
		{9.2E-4,0.00117},
		{9.1E-4,0.00116},
		{9.1E-4,0.00117},
		{8.9E-4,0.00116},
		{9.1E-4,0.00116},
		{9.1E-4,0.00117},
		{8.9E-4,0.00115},
		{9.0E-4,0.00117},
		{9.1E-4,0.00116},
		{9.1E-4,0.00117},
		{8.9E-4,0.00116},
		{9.0E-4,0.00116},
		{9.1E-4,0.00116},
		{9.1E-4,0.00116},
		{9.1E-4,0.00117},
		{9.0E-4,0.00115},
		{8.9E-4,0.00115},
		{8.6E-4,0.00115},
		{9.0E-4,0.00115},
		{8.9E-4,0.00114},
		{8.7E-4,0.00115},
		{9.0E-4,0.00116},
		{9.0E-4,0.00114},
		{8.8E-4,0.00115},
		{9.0E-4,0.00113},
		{8.9E-4,0.00114},
		{8.6E-4,0.00113},
		{8.9E-4,0.00113},
		{9.0E-4,0.00114},
		{8.8E-4,0.00112},
		{8.7E-4,0.00112}
		};
	
	private double mean;
	private double[] data;
	private double sign;
	private boolean isExponential;
	
	/**
	 * Constructor. Note that that MUST BE ordered in ascending order by the caller!
	 * @param data
	 * @param sign
	 */
	public ExponentialFitting(double[] data,double sign) {
		this.data = data;
		this.sign = sign;
		this.isExponential = false;
		
		calculateParameters();
	}
	
	private void calculateParameters() {
		mean = 0.0d;
		for(double el:data) {
			mean += el;
		}
		mean /= data.length;
	}
	
	public double[] getEstimatedParameters() {
		double[] result;
		
		result = new double[1];
		result[0] = 1d/mean;
		
		return result;
	}
	
	public boolean isLastRunFitted() {
		return isExponential;
	}
	
	public boolean isFitted() {
		double an,bn,mu,sigmasq;
		double numw,denw,wn,fn;
		long n = data.length;
		
		numw = Math.pow(mean - data[0], 2);
		
		denw = 0.0d;
		for(double el:data) {
			denw += Math.pow(el - mean,2);
		}
		
		wn = ((double) n/(n-1))*((double)numw/denw);
	
		if(data.length >= 1000) {
			double sx_limit, dx_limit;
			
			mu = ((double)n*(n-1))/(n+1);
			sigmasq = ((double) 4*Math.pow(n, 4)*(n-1))/(Math.pow(n+1, 2)*(n+2)*(n+3));
			an = ((double)Math.pow(n-1, 2)+1)/(n*(n-1));
			bn = (double) n/(n-1);
			
			//System.out.println("mu:"+mu+" sigmasq:"+sigmasq+" an:"+an+" bn:"+bn);
						
			fn = ((1/wn) - an - bn*mu)/(bn*Math.sqrt(sigmasq));
			
			//fixed values for 0.05 significativity
			//dx_limit = Stat.inverseNormalCDF(0, 1, 1-sign/2);
			dx_limit = 1.9599639815209002d;
			sx_limit = -dx_limit;
			
			isExponential = sx_limit < fn && fn < dx_limit;
			
			return isExponential;
		}
		else if(data.length >= 3) {
			isExponential = wn > crit_values[data.length-3][0] && wn < crit_values[data.length-3][1];
			
			return isExponential;
		}
		else
			return false;
	}
	
	public double[] generateQQPlot() {
		double[] y;
		
		//PlotGraph pg;
		
		int n = data.length;
		int i;
		
		y = new double[n];
		
		for(i=0;i<n;i++) {
			y[i] = -mean*Math.log(1d-((double) i+1)/((double) n+1));
			
			//System.out.println("Exponential Original value: " + data[i] + "  Inverted value: " + y[i]);
		}
		
		return y;
		
		//pg = new  PlotGraph(trasf_data, y);
		//pg.plot();
		
	}
}
