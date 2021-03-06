using namespace std;
#define EPS 1e-14
#define pi acos(-1.0)
#define mkp make_pair
typedef double dbl;
int cmp(dbl x)
{
if(fabs(x) < EPS) return 0;
return x < 0 ? -1 : 1;
}
struct PT{
dbl x, y;
PT(){x = y = 0; }
PT(dbl _x, dbl _y) {x = _x, y = _y; }
PT operator-(const PT &a) const{
return PT(x - a.x, y - a.y);
}
PT operator+(const PT &a) const{
return PT(x + a.x, y + a.y);
}
PT operator*(dbl a) const{
return PT(x * a, y * a);
}
PT operator/(dbl a) const{
return PT(x / a, y / a);
}
bool operator==(PT &a) const{
if(cmp(x - a.x) == 0 && cmp(y - a.y) == 0) return true;
return false;
}
dbl val()
{
return sqrt(x * x + y * y);
}
PT unit()
{
return (*this) / val();
}
void scan()
{
cin >> x >> y;
}
void print()
{
printf("(%.4f, %.4f)", x, y);
}
};
struct line{
dbl a, b, c;
};
dbl dist(PT a, PT b)
{
return (a - b).val();
}
dbl dist2(PT a, PT b)
{
a = a - b;
return a.x * a.x + a.y * a.y;
}
dbl dot(PT a, PT b)
{
return a.x * b.x + a.y * b.y;
}
dbl cross(PT a, PT b)
{
return a.x * b.y - a.y * b.x;
}
PT RotateCCW90(PT p)
{
return PT(-p.y,p.x);
}
PT RotateCW90(PT p)
{
return PT(p.y,-p.x);
}
PT RotateCCW(PT p, dbl t)
{
return PT(p.x*cos(t)-p.y*sin(t), p.x*sin(t)+p.y*cos(t));
}
PT RotateCW(PT p, dbl t)
{
return PT(p.x*cos(t)+p.y*sin(t), -p.x*sin(t)+p.y*cos(t));
}
// project point c onto line segment through a and b
PT ProjectPointSegment(PT a, PT b, PT c)
{
dbl r = dot(b-a,b-a);
if (fabs(r) < EPS) return a;
r = dot(c-a, b-a)/r;
if (r < 0) return a;
if (r > 1) return b;
return a + (b-a)*r;
}
// compute distance from c to segment between a and b
dbl DistancePointSegment(PT a, PT b, PT c)
{
return sqrtl(dist2(c, ProjectPointSegment(a, b, c)));
}
// returns bisector of angle YXZ
line bisector(PT Y, PT X, PT Z)
{
PT xy = (Y - X)/(Y - X).val();
PT xz = (Z - X)/(Z - X).val();
PT d = xy + xz;
line ret{d.y, -d.x, d.x * X.y - d.y * X.x};
return ret;
}
vector<PT> CircleLineIntersection(PT a, PT b, PT c, dbl r)
{
vector<PT>ret;
b=b-a;
a=a-c;
dbl A=dot(b, b);
dbl B=dot(a, b);
dbl C=dot(a, a)-r*r;
dbl D=B*B-A*C;
if(D<-EPS) return ret;
ret.push_back(c+a+b*(-B+sqrt(D+EPS))/A);
if(D>EPS) ret.push_back(c+a+b*(-B-sqrt(D))/A);
return ret;
}
vector<PT> CircleCircleIntersection(PT cn1, dbl r1, PT cn2, dbl r2)
{
dbl g1 = -cn1.x;
dbl f1 = -cn1.y;
dbl c1 =  cn1.x * cn1.x + cn1.y * cn1.y - r1*r1;
dbl g2 = -cn2.x;
dbl f2 = -cn2.y;
dbl c2 =  cn2.x * cn2.x + cn2.y * cn2.y - r2*r2;
PT a, b;
if(cmp(f1-f2)==0){
a.y = 6.0;
a.x = (c2 - c1 - 2*a.y*(f1-f2)) / (2 * (g1 - g2));
b.y = 13.0;
b.x = (c2 - c1 - 2*b.y*(f1-f2)) / (2 * (g1 - g2));
//        cout << "hi " << endl;
}
else{
a.x = 6;
a.y = (c2 - c1 - 2*a.x*(g1-g2)) / (2 * (f1 - f2));
b.x = 13;
b.y = (c2 - c1 - 2*b.x*(g1-g2)) / (2 * (f1 - f2));
}
//    a.print() ;
//    b.print() ;
return CircleLineIntersection(a, b, cn1, r1);
}
PT ComputeLineIntersection(PT a, PT b, PT c, PT d)
{
dbl a1=a.y-b.y;
dbl b1=b.x-a.x;
dbl c1=cross(a, b);
dbl a2=c.y-d.y;
dbl b2=d.x-c.x;
dbl c2=cross(c, d);
dbl D=a1*b2-a2*b1;
return PT((b1*c2-b2*c1)/D, (c1*a2-c2*a1)/D);
}
const dbl big = 100000000000000000.0;
dbl getarea(PT a, PT b, PT c)
{
return fabs(cross(b-a, c-a)) / 2.0;
}
vector<PT> CircleTouchingPoints(PT c, dbl r, PT a)
{
dbl d = dist(c, a);
dbl angle = asin(r/d);
dbl length = sqrt(d*d - r*r);
PT ac = c-a;
ac = RotateCCW(ac, angle);
ac = ac / ac.val() * length;
vector<PT> ret;
ret.push_back(a+ac);
ac = c-a;
ac = RotateCCW(ac, -angle);
ac = ac / ac.val() * length;
ret.push_back(ac + a);
return ret;
}
dbl CircleArcDistance(PT c, dbl r, PT a, PT b)
{
dbl d = dist(a, b);
dbl angle = acos((2.0*r*r - d*d) / (2.0*r*r));
return r * angle;
}
bool isAntiCW(PT a, PT b, PT c)
{
return cmp(cross(b-a, c-a)) >=0;
}
int main()
{
PT cn1, cn2;
dbl r1, r2;
cn1.scan();
scanf("%lf", &r1);
cn2.scan();
scanf("%lf", &r2);
vector<PT> pts = CircleCircleIntersection(cn1, r1, cn2, r2);
if(pts.size() < 2) {
dbl ans = 0.0;
if(cmp(dist(cn1, cn2) + r1 - r2) <= 0) ans = pi * r1 * r1;
if(cmp(dist(cn1, cn2) + r2 - r1) <= 0) ans = pi * r2 * r2;
printf("%.3f\n", ans), exit(0);
}
PT a = pts[0], b = pts[1];
//    a.print();
//    b.print();
dbl angle1 = atan2(cross(a-cn1, b-cn1), dot(a-cn1, b-cn1));
dbl angle2 = atan2(cross(a-cn2, b-cn2), dot(a-cn2, b-cn2));
if(cmp(angle1) < 0) angle1 += 2*pi;
if(cmp(angle2) < 0) angle2 += 2*pi;
if(isAntiCW(cn1, a, cn2) == false) angle1 = 2*pi - angle1;
if(isAntiCW(cn2, a, cn1) == false) angle2 = 2*pi - angle2;
dbl area = angle1 * r1 * r1 / 2.0 + angle2 * r2 * r2 / 2.0 - getarea(cn1, a, cn2) - getarea(cn2, b, cn1);
printf("%.3f\n", area);
return 0;
}
