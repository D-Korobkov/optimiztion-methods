package search;

import interfaces.MathFunction;
import interfaces.Search;


/**
 * Класс поиска методом Брента
 * @see search.AbstractSearch
 */
public class BrentSearch extends AbstractSearch {

    /**
     * Пропорции для золотого сечения - 1
     */
    //TODO: make abstract for GoldenSearch
    protected static final double K = (3 - Math.sqrt(5)) / 2;



    /**
     * Конструктор - создание объекта с заданными свойствами
     * @param function - функция, на которой ищут минимум
     * @param leftBorder - левая граница поиска
     * @param rightBorder - правая граница поиска
     * @param epsilon - точность вычислений
     */
    public BrentSearch(MathFunction function, double leftBorder, double rightBorder, double epsilon) {
        super(function, leftBorder, rightBorder, epsilon);

    }


    /**
     * Функция поиска минимума {@link Search#searchMinimum()}
     * @return возвращает точку минимума на промежутке
     */
    @Override
    public double searchMinimum() {

        //сетаем начальные значения
        double a = leftBorder;      //левая граница
        double c = rightBorder;     //правая граница
        double x = a + K * (c - a); //условный минимум
        double w = x;               //2-ой по минимальности
        double v = x;               //предыдущее значение w
        double fx = function.run(x);//значение функции в х
        double fw = fx;             //значение функции в w
        double fv = fx;             //значение функции в v

        double d = (c - a);
        double e = d;

        double g, tol;

        //double lastLen = c - a;
        //log(a, c, x, w, v, -1, fx, fw, fv, -1, lastLen, false);

        int count = 0;

        while (true) {

            //lastLen = c - a;
            g = e;
            e = d;
            double u = 0;
            boolean uIsGood = false;

            tol = epsilon * Math.abs(x) + epsilon / 10.0;

            //проверка на завершение
            if (Math.abs(x - (a + c) / 2) + (c - a) / 2 <= 2 * tol) {
                break;
            }

            //пытаемся использовать Метод Парабол
            if (x != w && x != v && w != v && fx != fv && fx != fw && fv != fw) {
                //find u (min of par on v,x,w)

                u = findMinParNoOrder(v, x, w, fv, fx, fw);

                //проверяем, что u попадает в диапазон
                if (a <= u && u <= c && Math.abs(u - x) < g / 2) {

                    //говорим что u нам подходит
                    uIsGood = true;

                    //если u оказался слишком близко к границе, то мы двигаем его в x
                    if (u - a < 2 * tol || c - u < 2 * tol) {
                        u = x - Math.signum(x - (a + c) / 2) * tol;
                    }
                }
            }

            //когда метод парабол не прошел, переходим на Золотое сечение
            if (!uIsGood) {
                if (x < (a + c) / 2) {
                    u = x + K * (c - x);
                    e = c - x;
                } else {
                    u = x - K * (x - a);
                    e = x - a;
                }
            }

            if (Math.abs(u - x) < tol) {
                u = x + Math.signum(u - x) * tol;
            }
            d = Math.abs(u - x);

            double fu = function.run(u);

            if (fu <= fx) {
                if (u >= x) {
                    a = x;
                } else {
                    c = x;
                }
                v = w;
                w = x;
                x = u;
                fv = fw;
                fw = fx;
                fx = fu;
            } else {
                if (u >= x) {
                    c = u;
                } else {
                    a = u;
                }
                if (fu <= fw || w == x) {
                    v = w;
                    w = u;
                    fv = fw;
                    fw = fu;
                } else if (fu <= fv || v == x || v == w) {
                    v = u;
                    fv = fu;
                }

            }
            count++;
            //log(a, c, x, w, v, u, fx, fw, fv, fu, lastLen, uIsGood);
        }

        System.out.println(count);

        return x;


    }

    /**
     * Поиск минимума параболы по 3-м точкам, x1 != x2 != x3
     * @param x1 первая точка
     * @param x2 вторая точка
     * @param x3 третья точка
     * @param f1 значение функции в первой точке
     * @param f2 значение функции во второй точке
     * @param f3 значение функции в третьей точке
     * @return минимум параболы
     */
    private double findMinParNoOrder(double x1, double x2, double x3, double f1, double f2, double f3) {
        if (x1 < x2 && x2 < x3) {
            return findMinPar(x1, x2, x3, f1, f2, f3);
        }
        if (x3 < x2 && x2 < x1) {
            return findMinPar(x3, x2, x1, f3, f2, f1);
        }
        if (x2 < x1 && x1 < x3) {
            return findMinPar(x2, x1, x3, f2, f1, f3);
        }
        if (x3 < x1 && x1 < x2) {
            return findMinPar(x3, x1, x2, f3, f1, f2);
        }
        if (x1 < x3 && x3 < x2) {
            return findMinPar(x1, x3, x2, f1, f3, f2);
        }
        if (x2 < x3 && x3 < x1) {
            return findMinPar(x2, x3, x1, f2, f3, f1);
        }
        return -1;
    }

    /**
     * Поиск минимума параболы по 3-м точкам, x1 < x2 < x3
     * @param x1 первая точка
     * @param x2 вторая точка
     * @param x3 третья точка
     * @param f1 значение функции в первой точке
     * @param f2 значение функции во второй точке
     * @param f3 значение функции в третьей точке
     * @return минимум параболы
     */
    //TODO: make it abstract for parabola
    private double findMinPar(double x1, double x2, double x3, double f1, double f2, double f3) {
        double a1 = (f2 - f1) / (x2 - x1);
        double a2 = ((f3 - f1) / (x3 - x1) - a1) / (x3 - x2);
        return (x1 + x2 - a1 / a2) / 2;
    }

    /*private void log(double a, double c, double x, double w, double v, double u, double fx, double fw, double fv, double fu, double lastLen, boolean methodFlag){
            System.out.println(String.format("[%f;%f] %f %f;%f %f;%f %f;%f %f;%f %b\n", a, c, (c-a)/lastLen, v, w, fv, fw, x, u, fx, fu, methodFlag));
            System.out.println(e.getMessage());

    }*/

}