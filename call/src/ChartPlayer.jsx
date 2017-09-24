import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { TiArrowUpThick, TiArrowRepeat } from 'react-icons/lib/ti';
//import ReactAnimationFrame from 'react-animation-frame';


class ChartPlayer extends Component {
  // onAnimationFrame(time) {
  //   // do something
  //   const progress = time / this.props.durationMs * 100;
  //   if (progress >= 100) {
  //     this.props.endAnimation();
  //   }
  // }

  constructor(props) {
    super(props);
  }

  // static childContextTypes = {
  //   reactIconBase: PropTypes.object
  // };

  // getChildContext() {
  //   return {
  //     reactIconBase: {
  //       color: 'blue',
  //     }
  //   }
  // }

  render() {
    const current = this.props.currentTime;
    const chart = this.props.chart;
    let actions = [];
    let actions2 = [];
    if (chart && current) {
      actions = chart.filter((t) => {
        return t - current <= 1000 && t - current >= -200;
      }).map((t) => { return t - current - 100; });;
      //actions2 = actions.map((t) => { return t - current - 100; });
    }
    // let arrowSize = 10;
    // if (actions2.length > 0) {
    //   arrowSize = (1000 - actions2[0]) / 1000 * 80 + arrowSize;
    // }
    const arrows = actions.map((t) => {
        const maxSize = 150;
        if (t >= 0) {
          return <TiArrowUpThick size={(1000 - t) / 1000 * maxSize + 1} color='blue'/>
        }
        else {
          return <TiArrowUpThick size={maxSize} color='GoldenRod'/>
        }
    });
    //const arrow = arrows.length > 0 ? arrows[0] : null; 
    return (
      <div>{arrows[0]}</div>
    );
  }
}

export default ChartPlayer;